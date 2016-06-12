#[macro_use]
extern crate log;
extern crate env_logger;
extern crate getopts;
extern crate hyper;
extern crate mio;
extern crate mioco;
extern crate rustc_serialize;

mod error;
mod service;
mod eventsource;

use std::env;
use std::collections::HashMap;
use getopts::Options;
use hyper::client::Client;
use mioco::sync::mpsc;
use service::{Service, Statistic, App};
use eventsource::{connect};
use error::{AutoscaleResult};

#[derive(Debug)]
enum Message {
    Update,
    Tick,
}

struct Autoscale {
    service: Service,
    apps: HashMap<String, App>,
    stats: HashMap<String, Statistic>,
}

impl Autoscale {
    fn update(&mut self) -> AutoscaleResult<()> {
        self.apps.clear();
        self.stats.clear();

        let apps = try!(self.service.get_apps());

        for id in apps {
            let app = try!(self.service.get_app(id.as_ref()));
            if app.is_some() {
                self.apps.insert(id, app.unwrap());
            }
        }

        Ok(())
    }

    fn scale(&mut self) -> AutoscaleResult<()> {
        let slaves = try!(self.service.get_slaves());

        for (id, app) in &self.apps {
            let stat = {
                let prev = self.stats.get(id);
                self.service.get_statistic(&app, &slaves, prev)
            };

            if stat.is_err() {
                continue;
            }

            let stat = stat.unwrap();
            if stat.cpu_usage > app.max_cpu_usage ||
               stat.mem_usage > app.max_mem_usage {
                try!(self.service.scale(&app));
            }

            info!("----------------------------------------");
            info!("App: {}", app.name);
            info!("Instances: {}", app.instances);
            info!("CPU: {}", stat.cpu_usage);
            info!("MEM: {}", stat.mem_usage);

            self.stats.insert(id.to_owned(), stat);
        }

        Ok(())
    }
}

fn main() {
    env_logger::init().unwrap();
    let args: Vec<String> = env::args().collect();

    let mut opts = Options::new();
    opts.reqopt("", "host", "Set marathon/mesos hostname", "172.17.42.1");
    opts.optopt("", "cpu-percent", "Set maximum CPU usage", "80");
    opts.optopt("", "mem-percent", "Set maximum memory usage", "80");
    opts.optopt("", "max", "Set maximum instances", "20");

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

    let mut handler = Autoscale {
        service: service,
        apps: HashMap::new(),
        stats: HashMap::new(),
    };

    mioco::start(move || {
        let (sender, receiver) = mpsc::channel::<Message>();
        mioco::spawn(move || loop {
            match receiver.recv() {
                Ok(Message::Tick) => handler.scale().unwrap(),
                Ok(Message::Update) => handler.update().unwrap(),
                Err(_) => {},
            }
        });

        mioco::spawn({
            let sender = sender.clone();
            move || connect("172.17.42.1:8080", |event| {
                if event.event == Some("status_update_event".to_string()) {
                    sender.send(Message::Update).unwrap();
                }
            })
        });

        sender.send(Message::Update).unwrap();

        loop {
            sender.send(Message::Tick).unwrap();
            mioco::sleep_ms(10000);
        }
    }).unwrap();
}
