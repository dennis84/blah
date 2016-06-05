extern crate hyper;
extern crate rustc_serialize;

use std::thread;
use std::time::Duration;
use std::io::prelude::*;
use std::collections::HashMap;
use hyper::client::Client;
use rustc_serialize::json::{self, Json};

mod error;

use error::{AutoscaleResult};

struct Config {
    host: String,
    app: String,
    mem_percent: f64,
    cpu_time: f64,
    multiplier: f64,
    max_instances: i32,
}

fn main() {
    let conf = Config {
        host: "172.17.42.1".to_string(),
        app: "api".to_string(),
        mem_percent: 80.0,
        cpu_time: 80.0,
        multiplier: 1.5,
        max_instances: 10,
    };

    let client = Client::new();
    let apps = get_apps(&client, &conf).unwrap();

    if ! &apps.contains(&conf.app) {
        println!("Could not find app: {}", &conf.app);
        ::std::process::exit(1);
    }

    let slaves = get_slaves(&client, &conf).unwrap();

    loop {
        let app = get_app(&client, &conf).unwrap();
        println!("Running instances: {}", &app.instances);

        let mut cpu_values: Vec<f64> = Vec::new();
        let mut mem_values: Vec<f64> = Vec::new();

        for (id, slave_id) in app.tasks.clone() {
            let url = slaves.get(&slave_id).unwrap().to_string();
            let stats = get_statistic(&client, &conf, url, id)
                            .unwrap().unwrap();
            cpu_values.push(stats.cpus_system_time_secs +
                            stats.cpus_user_time_secs);
            mem_values.push(100.0 * stats.mem_rss_bytes as f64 /
                            stats.mem_limit_bytes as f64);
        }

        let avg_cpu = cpu_values.iter().fold(0.0, |a, &b| a + b) /
                      cpu_values.len() as f64;
        let avg_mem = mem_values.iter().fold(0.0, |a, &b| a + b) /
                      mem_values.len() as f64;

        if avg_cpu > conf.cpu_time || avg_mem > conf.mem_percent {
            match scale_app(&client, &conf, &app) {
                Ok(_) => {
                    println!("Successfully scaled app: {}", conf.app);
                    println!("CPU: {} MEM: {}", avg_cpu, avg_mem);
                }
                Err(err) => {
                    println!("Autoscaling failed!: {}", err);
                    ::std::process::exit(1);
                }
            }
        } else {
            println!("No need to scale: {}", conf.app);
            println!("CPU: {} MEM: {}", avg_cpu, avg_mem);
        }

        thread::sleep(Duration::new(10, 0))
    }
}

#[derive(Debug)]
struct App {
    instances: i64,
    tasks: HashMap<String, String>,
}

fn get_app(client: &Client, conf: &Config)
           -> AutoscaleResult<App> {
    let url = format!("http://{}:8080/v2/apps/{}", &conf.host, &conf.app);
    let mut res = try!(client.get(&url).send());
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

fn get_apps(client: &Client, conf: &Config)
            -> AutoscaleResult<Vec<String>> {
    let url = format!("http://{}:8080/v2/apps", &conf.host);
    let mut res = try!(client.get(&url).send());
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

fn get_slaves(client: &Client, conf: &Config)
              -> AutoscaleResult<HashMap<String, String>> {
    let url = format!("http://{}:5050/master/slaves", &conf.host);
    let mut res = try!(client.get(&url).send());
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

#[derive(Debug, RustcDecodable)]
struct Statistic {
    cpus_limit: f64,
    cpus_system_time_secs: f64,
    cpus_user_time_secs: f64,
    mem_limit_bytes: i64,
    mem_rss_bytes: i64,
    timestamp: f64,
}

fn get_statistic(client: &Client, conf: &Config, slave: String, id: String)
                 -> AutoscaleResult<Option<Statistic>> {
    let url = format!("http://{}/monitor/statistics", &slave);
    let mut res = try!(client.get(&url).send());
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

fn scale_app(client: &Client, conf: &Config, app: &App)
             -> AutoscaleResult<()> {
    let instances = (app.instances as f64 * conf.multiplier).ceil() as i32;
    if instances > conf.max_instances {
        panic!("Reached maximum instances of: {}", conf.max_instances);
    }

    let body = format!(r#"{{"instances": {}}}"#, instances);
    let url = format!("http://{}:8080/v2/apps/{}", &conf.host, &conf.app);
    let mut res = try!(client.put(&url).body(&body).send());
    Ok(())
}
