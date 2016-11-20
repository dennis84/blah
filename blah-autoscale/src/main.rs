#[macro_use]
extern crate log;
extern crate env_logger;
extern crate getopts;
extern crate curl;
extern crate futures;
extern crate tokio_core;
extern crate tokio_curl;
extern crate rustc_serialize;
extern crate chrono;

mod service;
mod logger;

use std::env;
use std::thread;
use std::time::Duration;
use std::collections::HashMap;
use getopts::Options;
use rustc_serialize::json;
use futures::{Future};
use tokio_core::reactor::Core;
use service::{Service, Statistic};

fn main() {
    logger::new_logger();
    let args: Vec<String> = env::args().collect();

    let mut opts = Options::new();
    opts.reqopt("", "host", "Set marathon/mesos hostname", "172.17.42.1");
    opts.optopt("", "cpu-percent", "Set maximum CPU usage", "80");
    opts.optopt("", "mem-percent", "Set maximum memory usage", "80");
    opts.optopt("", "max", "Set maximum instances", "10");

    let matches = opts.parse(&args[1..]).unwrap_or_else(|_| {
        let brief = format!("Usage: autoscale [options]");
        error!("{}", opts.usage(&brief));
        ::std::process::exit(1);
    });

    let host = matches.opt_str("host").unwrap();

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
        .parse::<i32>()
        .unwrap();

    let multiplier = 1.5;

    let mut evloop = Core::new().unwrap();
    let mut service = Service::new(evloop.handle(),
                                   host.clone(),
                                   max_mem_usage,
                                   max_cpu_usage,
                                   multiplier,
                                   max_instances);

    let mut stats: HashMap<String, Statistic> = HashMap::new();

    loop {
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

            let app = app.unwrap();
            let stat = {
                let s = stats.get(&app.name);
                let f = service.get_statistic(&app, &slaves, s);
                evloop.run(f).unwrap()
            };

            if stat.cpu_usage > app.max_cpu_usage ||
               stat.mem_usage > app.max_mem_usage {
                info!("Scale {} application.", app.name);
                let f = service.scale(&app);
                evloop.run(f).unwrap();
            }

            info!("{}", json::encode(&AppInfo {
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

#[derive(Debug, RustcEncodable)]
pub struct AppInfo {
    pub app: String,
    pub instances: i64,
    pub max_instances: i32,
    pub cpu_usage: f64,
    pub mem_usage: f64,
}
