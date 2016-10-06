#[macro_use]
extern crate log;
extern crate env_logger;
extern crate getopts;
extern crate hyper;
extern crate url;
extern crate mio;
extern crate mioco;
extern crate rustc_serialize;

mod error;
mod service;
mod eventsource;
mod server;
mod autoscale;

use std::env;
use getopts::Options;
use hyper::client::Client;
use mioco::sync::mpsc;
use service::{Service};
use eventsource::{connect};
use server::*;
use autoscale::{Autoscale, ConsoleOutput, SSEOutput};

#[derive(Debug)]
enum Message {
    Update,
    Tick,
}

fn main() {
    env_logger::init().unwrap();
    let args: Vec<String> = env::args().collect();

    let mut opts = Options::new();
    //opts.reqopt("", "host", "Set marathon/mesos hostname", "172.17.42.1");
    opts.optopt("", "cpu-percent", "Set maximum CPU usage", "80");
    opts.optopt("", "mem-percent", "Set maximum memory usage", "80");
    opts.optopt("", "max", "Set maximum instances", "20");

    let matches = opts.parse(&args[1..]).unwrap_or_else(|_| {
        let brief = format!("Usage: autoscale [options]");
        error!("{}", opts.usage(&brief));
        ::std::process::exit(1);
    });

    //let host = matches.opt_str("host").unwrap();
    let host = "172.17.42.1".to_string();

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
        host: host.clone(),
        max_mem_usage: max_mem_usage,
        max_cpu_usage: max_cpu_usage,
        multiplier: 1.5,
        max_instances: max_instances,
        client: Client::new(),
    };

    let (sse, mut server) = Server::new().unwrap();
    let mut handler = Autoscale::new(service, SSEOutput {
        sender: sse,
    });

    mioco::start(move || {
        mioco::spawn({
            move || server.start()
        });

        let (sender, receiver) = mpsc::channel::<Message>();
        mioco::spawn({
            move || loop {
                match receiver.recv() {
                    Ok(Message::Tick) => handler.tick().unwrap_or_else(|e| {
                        debug!("Update failed {:?}. Continue ...", e);
                    }),
                    Ok(Message::Update) => handler.update().unwrap_or_else(|e| {
                        debug!("Tick failed {:?}. Continue ...", e);
                    }),
                    Err(_) => {},
                }
            }
        });

        mioco::spawn({
            let sender = sender.clone();
            move || connect(format!("http://{}:8080/v2/events", host), |e| {
                if e.event == Some("status_update_event".to_string()) {
                    sender.send(Message::Update).unwrap();
                }
            })
        });

        loop {
            sender.send(Message::Tick).unwrap();
            mioco::sleep_ms(10000);
        }
    }).unwrap();
}
