extern crate getopts;
extern crate hyper;
extern crate rustc_serialize;

use std::env;
use std::thread;
use std::time::Duration;
use std::io::prelude::*;
use std::collections::HashMap;
use getopts::Options;
use hyper::client::Client;
use rustc_serialize::json::{self, Json};

mod error;

use error::{AutoscaleResult};

fn main() {
    let args: Vec<String> = env::args().collect();

    let mut opts = Options::new();
    opts.reqopt("", "host", "Set marathon/mesos hostname", "172.17.42.1");
    opts.reqopt("", "app", "Set marathon app name", "api");
    opts.optopt("", "cpu-percent", "Set maximum CPU usage", "80");
    opts.optopt("", "mem-percent", "Set maximum memory usage", "80");
    opts.optopt("", "max", "Set maximum instances", "20");

    let matches = opts.parse(&args[1..]).unwrap_or_else(|_| {
        let brief = format!("Usage: autoscale [options]");
        print!("{}", opts.usage(&brief));
        ::std::process::exit(1);
    });

    let host = matches.opt_str("host").unwrap();
    let app = matches.opt_str("app").unwrap();

    let max_mem_usage = matches
        .opt_str("mem-percent")
        .unwrap_or("80".to_string())
        .parse::<f64>()
        .unwrap();

    let max_cpu_usage = matches
        .opt_str("cpu-percent")
        .unwrap_or("80".to_string())
        .parse::<f64>()
        .unwrap();

    let max_instances = matches
        .opt_str("max")
        .unwrap_or("10".to_string())
        .parse::<i32>()
        .unwrap();

    let service = Service {
        host: host,
        app: app,
        max_mem_usage: max_mem_usage,
        max_cpu_usage: max_cpu_usage,
        multiplier: 1.5,
        max_instances: max_instances,
        client: Client::new(),
    };

    println!("Marathon/Mesos host: {}", service.host);
    println!("Marathon app: {}", service.app);
    println!("Max memory usage: {}", service.max_mem_usage);
    println!("Max CPU usage: {}", service.max_cpu_usage);
    println!("Max instances: {}", service.max_instances);
    println!("Multiplier: {}", service.multiplier);

    let apps = service.get_apps().unwrap();

    if ! &apps.contains(&service.app) {
        println!("Could not find app: {}", &service.app);
        ::std::process::exit(1);
    }

    let slaves = service.get_slaves().unwrap();
    let mut prev: Option<Statistic> = None;

    loop {
        let app = service.get_app().unwrap();
        println!("-------------------------------------");
        println!("Running instances: {}", &app.instances);

        let stat = service.get_statistic(&app, &slaves, prev).unwrap();

        if stat.cpu_usage > service.max_cpu_usage ||
           stat.mem_usage > service.max_mem_usage {
            match service.scale(&app) {
                Ok(_) => {
                    println!("Successfully scaled app: {}", service.app);
                    println!("CPU: {} MEM: {}", stat.cpu_usage, stat.mem_usage);
                }
                Err(err) => {
                    println!("Autoscaling failed!: {}", err);
                    ::std::process::exit(1);
                }
            }
        } else {
            println!("No need to scale: {}", service.app);
            println!("CPU: {} MEM: {}", stat.cpu_usage, stat.mem_usage);
        }

        prev = Some(stat);
        thread::sleep(Duration::new(10, 0))
    }
}

struct Service {
    host: String,
    app: String,
    max_mem_usage: f64,
    max_cpu_usage: f64,
    multiplier: f64,
    max_instances: i32,
    client: Client,
}

#[derive(Debug)]
struct App {
    instances: i64,
    tasks: HashMap<String, String>,
}

#[derive(Debug)]
struct Statistic {
    timestamp: f64,
    cpu_time: f64,
    cpu_usage: f64,
    mem_usage: f64,
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
    fn get_apps(&self) -> AutoscaleResult<Vec<String>> {
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

    fn get_app(&self) -> AutoscaleResult<App> {
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

    fn get_slaves(&self) -> AutoscaleResult<HashMap<String, String>> {
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

    fn get_statistic(&self, app: &App, slaves: &HashMap<String, String>,
                     prev: Option<Statistic>) -> AutoscaleResult<Statistic> {
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

    fn scale(&self, app: &App) -> AutoscaleResult<()> {
        let instances = (app.instances as f64 * self.multiplier).ceil() as i32;
        if instances > self.max_instances {
            panic!("Reached maximum instances of: {}", self.max_instances);
        }

        let body = format!(r#"{{"instances": {}}}"#, instances);
        let url = format!("http://{}:8080/v2/apps/{}", &self.host, &self.app);
        try!(self.client.put(&url).body(&body).send());
        Ok(())
    }
}
