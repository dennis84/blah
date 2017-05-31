#[macro_use] extern crate log;
#[macro_use] extern crate serde_json;
#[macro_use] extern crate serde_derive;
extern crate env_logger;
extern crate getopts;
extern crate curl;
extern crate futures;
extern crate tokio_core;
extern crate tokio_curl;
extern crate logstash_format;

mod service;

use std::env;
use std::thread;
use std::time::Duration;
use std::collections::HashMap;
use getopts::Options;
use futures::Future;
use tokio_core::reactor::Core;
use service::{Service, Statistic};

fn main() {
    logstash_format::new_builder(json!({
        "app": "autoscale",
    })).init().unwrap();

    let args: Vec<String> = env::args().collect();

    let mut opts = Options::new();
    opts.optopt("", "marathon", "Set marathon url", "http://localhost:8080");
    opts.optopt("", "mesos", "Set mesos url", "http://localhost:5050");
    opts.optopt("", "cpu-percent", "Set maximum CPU usage", "80");
    opts.optopt("", "mem-percent", "Set maximum memory usage", "80");
    opts.optopt("", "max", "Set maximum instances", "10");

    let matches = opts.parse(&args[1..]).unwrap();
    let marathon_url = matches
        .opt_str("marathon")
        .unwrap_or("http://localhost:8080".to_string());

    let mesos_url = matches
        .opt_str("mesos")
        .unwrap_or("http://localhost:5050".to_string());

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
        .unwrap_or("1".to_string())
        .parse::<i64>()
        .unwrap();

    let multiplier = 1.5;

    let mut evloop = Core::new().unwrap();
    let mut service = Service::new(evloop.handle(),
                                   marathon_url.clone(),
                                   mesos_url.clone(),
                                   max_mem_usage,
                                   max_cpu_usage,
                                   multiplier,
                                   max_instances);

    let mut stats: HashMap<String, Statistic> = HashMap::new();

    loop {
        info!("Tick.");

        let apps = {
            let f = service.get_apps().and_then(|apps| {
                futures::collect(apps.iter().map(|id| {
                    service.get_app(id.as_ref())
                }).collect::<Vec<_>>())
            });
            evloop.run(f).unwrap()
        };

        let slaves = {
            let f = service.get_slaves();
            evloop.run(f).unwrap()
        };

        for app in apps {
            if app.is_none() {
                continue;
            }
            info!("sasdsd 1");

            let app = app.unwrap();
            let stat = {
                let s = stats.get(&app.name);
                let f = service.get_statistic(&app, &slaves, s);
                evloop.run(f).unwrap()
            };
            info!("sasdsd 2");

            if stat.cpu_usage > app.max_cpu_usage ||
               stat.mem_usage > app.max_mem_usage {
                info!("Scale {} application.", app.name);
                let f = service.scale(&app);
                evloop.run(f).unwrap();
            }

            info!("sasdsd 3");
            info!("{}", serde_json::to_string(&AppInfo {
                app: app.name.to_owned(),
                instances: app.instances,
                max_instances: app.max_instances,
                cpu_usage: stat.cpu_usage,
                mem_usage: stat.mem_usage,
            }).unwrap());

            stats.insert(app.name, stat);
        }

        thread::sleep(Duration::from_millis(10000));
    }
}

#[derive(Debug, Serialize)]
pub struct AppInfo {
    pub app: String,
    pub instances: i64,
    pub max_instances: i64,
    pub cpu_usage: f64,
    pub mem_usage: f64,
}
