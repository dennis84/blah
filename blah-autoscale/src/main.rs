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
use service::{Service};
use eventsource::{connect};
use server::*;
use autoscale::{Autoscale, Message, AppInfo};
use mio::*;
use mio::channel::{channel, Sender, Receiver};
use std::thread;
use rustc_serialize::json::{encode};

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

    let mut autoscale = Autoscale::new(service);

    const TIMER: Token = Token(0);
    const MARATHON: Token = Token(1);

    let mut poll = Poll::new().unwrap();

    let (timer_sender, timer_receiver) = channel::<Message>();
    poll.register(&timer_receiver, TIMER, Ready::readable(),
                  PollOpt::edge()).unwrap();

    let (marathon_sender, marathon_receiver) = channel::<Message>();
    poll.register(&marathon_receiver, MARATHON, Ready::readable(),
                  PollOpt::edge()).unwrap();

    thread::spawn(move || loop {
        timer_sender.send(Message::Tick).unwrap();
        thread::sleep_ms(10000);
    });

    thread::spawn(move || connect(format!("http://{}:8080/v2/events", host), |e| {
        if e.event == Some("status_update_event".to_string()) {
            marathon_sender.send(Message::Update).unwrap();
        }
    }));

    let (server_sender, mut server) = Server::new().unwrap();
    let is_sse = true;

    if is_sse {
        thread::spawn(move || server.start());
    }

    let mut events = Events::with_capacity(1024);
    loop {
        poll.poll(&mut events, None).unwrap();
        for event in events.iter() {
            match event.token() {
                TIMER => match timer_receiver.try_recv() {
                    Ok(_) => match autoscale.tick() {
                        Ok(apps) => {
                            log_apps(&apps);
                            if is_sse {
                                server_sender.send(encode(&apps).unwrap()).unwrap();
                            }
                        }
                        Err(e) => debug!("Tick failed {:?}.", e),
                    },
                    _ => {}
                },
                MARATHON => match marathon_receiver.try_recv() {
                    Ok(_) => autoscale.update().unwrap_or_else(|e| {
                        debug!("Update failed {:?}. Continue ...", e);
                    }),
                    _ => {}
                },

                _ => {},
            }
        }
    }
}

fn log_apps(apps: &Vec<AppInfo>) {
    for app in apps.iter() {
        info!("----------------------------------------");
        info!("App: {}", app.app);
        info!("Instances: {}/{}", app.instances, app.max_instances);
        info!("CPU: {}", app.cpu_usage);
        info!("MEM: {}", app.mem_usage);
    }
}
