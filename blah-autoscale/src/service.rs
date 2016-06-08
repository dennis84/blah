use std::io::prelude::*;
use std::collections::HashMap;
use hyper::client::Client;
use rustc_serialize::json::{self, Json};
use error::AutoscaleResult;

pub struct Service {
    pub host: String,
    pub app: String,
    pub max_mem_usage: f64,
    pub max_cpu_usage: f64,
    pub multiplier: f64,
    pub max_instances: i32,
    pub client: Client,
}

#[derive(Debug)]
pub struct App {
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

impl Service {
    pub fn get_apps(&self) -> AutoscaleResult<Vec<String>> {
        let url = format!("http://{}:8080/v2/apps", &self.host);
        let mut res = try!(self.client.get(&url).send());
        let mut buf = String::new();
        res.read_to_string(&mut buf).unwrap();

        let data = Json::from_str(&buf).unwrap();
        let data = data.as_object().unwrap();
        let data = data.get("apps").unwrap();
        let data = data.as_array().unwrap();

        Ok(data.iter().map(|x| {
            let id = x.find("id").unwrap();
            let id = id.as_string().unwrap();
            id[1..].to_string()
        }).collect())
    }

    pub fn get_app(&self) -> AutoscaleResult<App> {
        let url = format!("http://{}:8080/v2/apps/{}", &self.host, &self.app);
        let mut res = try!(self.client.get(&url).send());
        let mut buf = String::new();
        try!(res.read_to_string(&mut buf));
        let data = try!(Json::from_str(&buf));

        let instances = data.find_path(&["app", "instances"]).unwrap();
        let instances = instances.as_i64().unwrap();

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

        Ok(App {
            instances: instances,
            tasks: tasks,
        })
    }

    pub fn get_slaves(&self) -> AutoscaleResult<HashMap<String, String>> {
        let url = format!("http://{}:5050/master/slaves", &self.host);
        let mut res = try!(self.client.get(&url).send());
        let mut buf = String::new();
        try!(res.read_to_string(&mut buf));

        let data = try!(Json::from_str(&buf));
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

        Ok(slaves)
    }

    pub fn get_statistic(&self, app: &App,
                         slaves: &HashMap<String, String>,
                         prev: Option<Statistic>)
                         -> AutoscaleResult<Statistic> {
        let mut mems: Vec<f64> = Vec::new();
        let mut cpus: Vec<f64> = Vec::new();
        let mut timestamp: f64 = 0.0;
        let mut cpu_usage: f64 = 0.0;

        for (id, slave_id) in &app.tasks {
            let url = slaves.get::<String>(&slave_id).unwrap().to_string();
            let task = self.get_task_statistic(url, id).unwrap().unwrap();
            timestamp = task.timestamp;
            cpus.push(task.cpus_user_time_secs + task.cpus_system_time_secs);
            mems.push(100.0 * task.mem_rss_bytes as f64 /
                      task.mem_limit_bytes as f64);
        }

        let mem_usage = mems.iter()
            .fold(0.0, |a, &b| a + b) / mems.len() as f64;

        let cpu_time = cpus.iter()
            .fold(0.0, |a, &b| a + b) / cpus.len() as f64;

        if prev.is_some() {
            let prev = prev.unwrap();
            let sampling_duration = timestamp - prev.timestamp;
            let cpu_time_usage = cpu_time - prev.cpu_time;
            cpu_usage = cpu_time_usage / sampling_duration * 100.0;
        }

        Ok(Statistic {
            timestamp: timestamp,
            cpu_time: cpu_time,
            mem_usage: mem_usage,
            cpu_usage: cpu_usage,
        })
    }

    pub fn scale(&self, app: &App) -> AutoscaleResult<()> {
        let instances = (app.instances as f64 * self.multiplier).ceil() as i32;
        if instances > self.max_instances {
            panic!("Reached maximum instances of: {}", self.max_instances);
        }

        let body = format!(r#"{{"instances": {}}}"#, instances);
        let url = format!("http://{}:8080/v2/apps/{}", &self.host, &self.app);
        try!(self.client.put(&url).body(&body).send());
        Ok(())
    }

    fn get_task_statistic(&self, slave: String, id: &str)
                          -> AutoscaleResult<Option<TaskStatistic>> {
        let url = format!("http://{}/monitor/statistics", &slave);
        let mut res = try!(self.client.get(&url).send());
        let mut buf = String::new();
        try!(res.read_to_string(&mut buf));

        let data = try!(Json::from_str(&buf));
        let data = data.as_array().unwrap();

        let statistic = data.iter().find(|x| {
            let executor_id = x.find("executor_id").unwrap();
            let executor_id = executor_id.as_string().unwrap();
            id == executor_id.to_string()
        });

        Ok(statistic.map(|x| {
            let s = x.find("statistics").unwrap();
            json::decode(&s.to_string()).unwrap()
        }))
    }
}
