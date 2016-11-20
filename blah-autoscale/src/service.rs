use std::io::Read;
use std::sync::{Arc, Mutex};
use std::collections::HashMap;
use curl::easy::{Easy, List};
use futures::{self, Future, BoxFuture};
use tokio_core::reactor::{Handle, Core};
use tokio_curl::{Session, PerformError};
use rustc_serialize::json::{self, Json};

pub type Fut<T> = BoxFuture<T, PerformError>;

#[derive(Debug)]
pub struct App {
    pub name: String,
    pub max_mem_usage: f64,
    pub max_cpu_usage: f64,
    pub max_instances: i32,
    pub instances: i64,
    pub tasks: HashMap<String, String>,
}

#[derive(Debug)]
pub struct Statistic {
    pub timestamp: f64,
    pub cpu_time: f64,
    pub cpu_usage: f64,
    pub mem_usage: f64,
}

#[derive(Debug, RustcDecodable)]
struct TaskStatistic {
    cpus_limit: f64,
    cpus_system_time_secs: f64,
    cpus_user_time_secs: f64,
    mem_limit_bytes: i64,
    mem_rss_bytes: i64,
    timestamp: f64,
}

pub struct Service {
    handle: Handle,
    host: String,
    max_mem_usage: f64,
    max_cpu_usage: f64,
    multiplier: f64,
    max_instances: i32,
}

impl Service {
    pub fn new(handle: Handle, host: String,
               max_mem_usage: f64, max_cpu_usage: f64,
               multiplier: f64, max_instances: i32)
               -> Service {
        Service {
            handle: handle,
            host: host,
            max_mem_usage: max_mem_usage,
            max_cpu_usage: max_cpu_usage,
            multiplier: multiplier,
            max_instances: max_instances,
        }
    }

    pub fn get_apps(&mut self) -> Fut<Vec<String>> {
        let url = format!("http://{}:8080/v2/apps", &self.host);
        self.send_get(&url).map(|body| {
            let data = Json::from_str(&body).unwrap();
            let data = data.as_object().unwrap();
            let data = data.get("apps").unwrap();
            let data = data.as_array().unwrap();
            let mut apps = Vec::new();

            for x in data.iter() {
                let id = x.find("id").unwrap();
                let id = id.as_string().unwrap();
                apps.push(id[1..].to_string());
            }

            apps
        }).boxed()
    }

    pub fn get_app(&mut self, app: &str) -> Fut<Option<App>> {
        let url = format!("http://{}:8080/v2/apps/{}", &self.host, &app);
        let app = app.to_string();
        let mut max_instances = self.max_instances.clone();
        let mut max_mem_usage = self.max_mem_usage.clone();
        let mut max_cpu_usage = self.max_cpu_usage.clone();

        self.send_get(&url).map(move |body| {
            let data = Json::from_str(&body).unwrap();
            let instances = data.find_path(&["app", "instances"]).unwrap();
            let instances = instances.as_i64().unwrap();

            let labels = data.find_path(&["app", "labels"]).unwrap();
            let labels = labels.as_object().unwrap();

            for (label, value) in labels {
                match (label.as_ref(), value) {
                    ("AUTOSCALE_MAX_INSTANCES", &Json::String(ref v)) => {
                        max_instances = v.parse::<i32>().unwrap();
                    }
                    ("AUTOSCALE_MEM_PERCENT", &Json::String(ref v)) => {
                        max_mem_usage = v.parse::<f64>().unwrap();
                    }
                    ("AUTOSCALE_CPU_PERCENT", &Json::String(ref v)) => {
                        max_cpu_usage = v.parse::<f64>().unwrap();
                    }
                    _ => {}
                }
            }

            let xs = data.find_path(&["app", "tasks"]).unwrap();
            let xs = xs.as_array().unwrap();
            let mut tasks = HashMap::new();

            for x in xs.iter() {
                let id = x.find("id").unwrap();
                let id = id.as_string().unwrap();
                let slave_id = x.find("slaveId").unwrap();
                let slave_id = slave_id.as_string().unwrap();
                tasks.insert(id.to_string(), slave_id.to_string());
            }

            Some(App {
                name: app,
                max_instances: max_instances,
                max_mem_usage: max_mem_usage,
                max_cpu_usage: max_cpu_usage,
                instances: instances,
                tasks: tasks,
            })
        }).boxed()
    }

    pub fn get_slaves(&mut self) -> Fut<HashMap<String, String>> {
        let url = format!("http://{}:5050/master/slaves", &self.host);
        self.send_get(&url).map(|body| {
            let data = Json::from_str(&body).unwrap();
            let data = data.as_object().unwrap();
            let data = data.get("slaves").unwrap();
            let data = data.as_array().unwrap();
            let mut slaves = HashMap::new();

            for slave in data.iter() {
                let id = slave.find("id").unwrap();
                let id = id.as_string().unwrap();
                let pid = slave.find("pid").unwrap();
                let pid = pid.as_string().unwrap();
                slaves.insert(id.to_string(), pid.to_string());
            }

            slaves
        }).boxed()
    }

    pub fn get_statistic(&mut self, app: &App,
                         slaves: &HashMap<String, String>,
                         prev: Option<&Statistic>)
                         -> Fut<Statistic> {
        let mut futs = Vec::new();

        for (id, slave_id) in &app.tasks {
            let url = slaves.get::<String>(&slave_id).unwrap().to_string();
            futs.push(self.get_task_statistic(url, id));
        }

        let mut prev_timestamp = 0.0;
        let mut prev_cpu_time = 0.0;

        if let Some(p) = prev {
            prev_timestamp = p.timestamp;
            prev_cpu_time = p.cpu_time;
        }

        futures::collect(futs).map(move |tasks| {
            let mut mems: Vec<f64> = Vec::new();
            let mut cpus: Vec<f64> = Vec::new();
            let mut timestamp: f64 = 0.0;

            for task in tasks {
                let task = task.unwrap();
                timestamp = task.timestamp;
                cpus.push(task.cpus_user_time_secs + task.cpus_system_time_secs);
                mems.push(100.0 * task.mem_rss_bytes as f64 /
                          task.mem_limit_bytes as f64);
            }

            let mem_usage = mems.iter()
                .fold(0.0, |a, &b| a + b) / mems.len() as f64;

            let cpu_time = cpus.iter()
                .fold(0.0, |a, &b| a + b) / cpus.len() as f64;

            let sampling_duration = timestamp - prev_timestamp;
            let cpu_time_usage = cpu_time - prev_cpu_time;
            let cpu_usage = cpu_time_usage / sampling_duration * 100.0;

            Statistic {
                timestamp: timestamp,
                cpu_time: cpu_time,
                mem_usage: mem_usage,
                cpu_usage: cpu_usage,
            }
        }).boxed()
    }

    pub fn scale(&mut self, app: &App) -> Fut<()> {
        let instances = (app.instances as f64 * self.multiplier).ceil() as i32;
        if instances > app.max_instances {
            info!("Cannot scale {}, reached maximum instances of: {}",
                  app.name, app.max_instances);
            return futures::done(Ok(())).boxed();
        }

        let url = format!("http://{}:8080/v2/apps/{}", &self.host, &app.name);
        let body = format!(r#"{{"instances": {}}}"#, instances);
        let session = Session::new(self.handle.clone());

        let mut req = Easy::new();
        req.url(&url).unwrap();
        req.put(true).unwrap();

        let mut list = List::new();
        list.append("Content-Type: application/json").unwrap();
        req.http_headers(list).unwrap();

        req.post_field_size(body.as_bytes().len() as u64).unwrap();
        req.read_function(move |buf| {
            let mut data = body.as_bytes();
            Ok(data.read(buf).unwrap_or(0))
        }).unwrap();

        session.perform(req).map(|mut r| {
            info!("Scaling response code: {}", r.response_code().unwrap());
        }).boxed()
    }

    fn get_task_statistic(&mut self, slave: String, id: &str)
                          -> Fut<Option<TaskStatistic>> {
        let url = format!("http://{}/monitor/statistics", &slave);
        let id = id.to_string();
        self.send_get(&url).map(move |body| {
            let data = Json::from_str(&body).unwrap();
            let data = data.as_array().unwrap();

            let statistic = data.iter().find(|x| {
                let executor_id = x.find("executor_id").unwrap();
                let executor_id = executor_id.as_string().unwrap();
                id == executor_id.to_string()
            });

            statistic.map(|x| {
                let s = x.find("statistics").unwrap();
                json::decode(&s.to_string()).unwrap()
            })
        }).boxed()
    }

    fn send_get(&mut self, url: &str) -> Fut<String> {
        let session = Session::new(self.handle.clone());
        let response = Arc::new(Mutex::new(Vec::new()));
        let headers = Arc::new(Mutex::new(Vec::new()));

        let mut req = Easy::new();
        req.get(true).unwrap();
        req.url(url).unwrap();
        let response2 = response.clone();

        req.write_function(move |data| {
            response2.lock().unwrap().extend_from_slice(data);
            Ok(data.len())
        }).unwrap();

		let headers2 = headers.clone();
		req.header_function(move |header| {
			headers2.lock().unwrap().push(header.to_vec());
			true
		}).unwrap();

        session.perform(req).map(move |_| {
			let response = response.lock().unwrap();
			let response = String::from_utf8_lossy(&response);
            response.into_owned()
		}).boxed()
    }
}

#[test]
#[ignore]
fn test() {
    let host = "172.17.42.1";
    let mut evloop = Core::new().unwrap();
    let mut service = Service::new(evloop.handle(),
                                   host.to_string(),
                                   80.0, 80.0, 1.5, 10);

    let fut = service.get_slaves();
    let slaves =  evloop.run(fut).unwrap();

    let fut = service.get_apps();
    let apps =  evloop.run(fut).unwrap();
    for id in apps {
        let fut = service.get_app(&id);
        let app =  evloop.run(fut).unwrap().unwrap();

        let fut = service.get_statistic(&app, &slaves, None);
        let stat =  evloop.run(fut).unwrap();

        if app.name == "api" {
            let fut = service.scale(&app);
            evloop.run(fut).unwrap();
        }
    }
}
