extern crate getopts;
extern crate hyper;
extern crate rustc_serialize;

use std::env;
use std::thread;
use std::time::Duration;
use getopts::Options;
use hyper::client::Client;

mod error;
mod service;
use service::{Service, Statistic};

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
        let app = service.get_app(&service.app).unwrap();
        println!("-------------------------------------");
        println!("Running instances: {}", app.instances);

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
