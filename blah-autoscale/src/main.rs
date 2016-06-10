extern crate getopts;
extern crate hyper;
extern crate mio;
extern crate rustc_serialize;

use std::env;
use std::thread;
use std::time::Duration;
use std::collections::HashMap;
use std::sync::mpsc::channel;
use getopts::Options;
use hyper::client::Client;
use rustc_serialize::json::{self, Json};

mod error;
mod service;
mod eventsource;
use service::{Service, Statistic, App};
use error::{AutoscaleResult, Error};
use eventsource::{connect, Event};

fn main() {
    let args: Vec<String> = env::args().collect();

    let mut opts = Options::new();
    opts.reqopt("", "host", "Set marathon/mesos hostname", "172.17.42.1");
    opts.optopt("", "cpu-percent", "Set maximum CPU usage", "80");
    opts.optopt("", "mem-percent", "Set maximum memory usage", "80");
    opts.optopt("", "max", "Set maximum instances", "20");

    let matches = opts.parse(&args[1..]).unwrap_or_else(|_| {
        let brief = format!("Usage: autoscale [options]");
        print!("{}", opts.usage(&brief));
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
        .unwrap_or("10".to_string())
        .parse::<i32>()
        .unwrap();

    let service = Service {
        host: host,
        max_mem_usage: max_mem_usage,
        max_cpu_usage: max_cpu_usage,
        multiplier: 1.5,
        max_instances: max_instances,
        client: Client::new(),
    };

    println!("Marathon/Mesos host: {}", service.host);
    println!("Max memory usage: {}", service.max_mem_usage);
    println!("Max CPU usage: {}", service.max_cpu_usage);
    println!("Max instances: {}", service.max_instances);
    println!("Multiplier: {}", service.multiplier);

    let (tx, rx) = channel();
    let tx2 = tx.clone();

    tx.send(Action::StartApplication(service.get_apps().unwrap())).unwrap();

    thread::spawn(move || {
        let client = connect("172.17.42.1:8080", move |event| {
            match filtered(&event) {
                Ok(action) => { tx.send(action).unwrap() }
                _ => ()
            }
        }).unwrap();
    });

    thread::spawn(move || {
        loop {
            thread::sleep(Duration::new(10, 0));
            tx2.send(Action::Tick).unwrap();
        }
    });

    let mut apps: HashMap<String, App> = HashMap::new();
    let mut stats: HashMap<String, Statistic> = HashMap::new();
    let slaves = service.get_slaves().unwrap();

    loop {
        match rx.recv() {
            Ok(Action::Tick) => {}
            Ok(Action::StartApplication(xs)) => {
                for x in xs {
                    let app = service.get_app(x.as_ref()).unwrap().unwrap();
                    let _ = apps.insert(x, app);
                }
            }
            Ok(Action::StopApplication(xs)) => {
                for x in xs {
                    let _ = apps.remove(&x[..]);
                }
            }
            _ => {
                continue;
            }
        }

        for (id, app) in &apps {
            println!("------------------------------------");
            println!("App: {}", app.name);
            println!("Running instances: {}", app.instances);

            let stat = {
                let prev = stats.get(id);
                service.get_statistic(&app, &slaves, prev).unwrap()
            };

            if stat.cpu_usage > app.max_cpu_usage ||
               stat.mem_usage > app.max_mem_usage {
                match service.scale(&app) {
                    Ok(_) => {
                        println!("Successfully scaled app: {}", app.name);
                        println!("CPU: {} MEM: {}", stat.cpu_usage, stat.mem_usage);
                    }
                    Err(err) => {
                        println!("Autoscaling failed!: {}", err);
                        ::std::process::exit(1);
                    }
                }
            } else {
                println!("No need to scale: {}", app.name);
                println!("CPU: {} MEM: {}", stat.cpu_usage, stat.mem_usage);
            }

            stats.insert(id.to_owned(), stat);
        }
    }
}

enum Action {
    StartApplication(Vec<String>),
    StopApplication(Vec<String>),
    Tick,
    Noop,
}

fn parse_apps(data: &Json) -> AutoscaleResult<Vec<String>> {
    let mut apps = Vec::new();
    let xs = try!(data.as_array().ok_or(Error::Parse));
    for x in xs.iter() {
        let labels = try!(x.find("labels").ok_or(Error::Parse));
        let labels = try!(labels.as_object().ok_or(Error::Parse));
        if labels.iter().find(|&(x,y)| x.starts_with("AUTOSCALE_")).is_none() {
            continue;
        }

        let name = try!(x.find("id").ok_or(Error::Parse));
        let name = try!(name.as_string().ok_or(Error::Parse));
        apps.push(name[1..].to_string());
    }

    Ok(apps)
}

fn filtered(event: &Event) -> AutoscaleResult<Action> {
    if event.event != Some("deployment_success".to_string()) {
        return Ok(Action::Noop);
    }

    let data = try!(Json::from_str(&event.data));
    let steps = try!(data.find_path(&["plan", "steps"]).ok_or(Error::Parse));
    let steps = try!(steps.as_array().ok_or(Error::Parse));
    for step in steps.iter() {
        let actions = try!(step.find("actions").ok_or(Error::Parse));
        let actions = try!(actions.as_array().ok_or(Error::Parse));
        for action in actions.iter() {
            match action.find("action") {
                Some(&Json::String(ref n)) if n == "StartApplication" => {
                    let xs = try!(data.find_path(
                        &["plan", "target", "apps"]).ok_or(Error::Parse));
                    return Ok(Action::StartApplication(try!(parse_apps(xs))))
                }
                Some(&Json::String(ref n)) if n == "StopApplication" => {
                    let xs = try!(data.find_path(
                        &["plan", "original", "apps"]).ok_or(Error::Parse));
                    return Ok(Action::StopApplication(try!(parse_apps(xs))))
                }
                _ => ()
            }
        }
    }

    Ok(Action::Noop)
}
