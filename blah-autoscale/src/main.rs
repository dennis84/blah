#[macro_use]
extern crate log;
extern crate env_logger;
extern crate getopts;
extern crate hyper;
extern crate url;
extern crate mio;
extern crate rustc_serialize;
extern crate httparse;

mod error;
mod service;
mod eventsource;
mod autoscale;

use std::env;
use std::thread;
use std::time::Duration;
use getopts::Options;
use mio::*;
use mio::channel::channel;
use service::Service;
use eventsource::connect;
use autoscale::{Autoscale, AppInfo};

fn main() {
    env_logger::init().unwrap();
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

    let service = Service::new(host.clone(),
                               max_mem_usage,
                               max_cpu_usage,
                               multiplier,
                               max_instances);

    let mut autoscale = Autoscale::new(service);
    let (timer_tx, timer_rx) = channel::<Message>();
    let (marathon_tx, marathon_rx) = channel::<Message>();

    let poll = Poll::new().unwrap();

    const TIMER: Token = Token(0);
    const MARATHON: Token = Token(1);

    poll.register(&timer_rx, TIMER, Ready::readable(),
                  PollOpt::edge()).unwrap();
    poll.register(&marathon_rx, MARATHON, Ready::readable(),
                  PollOpt::edge()).unwrap();

    thread::spawn(move || loop {
        timer_tx.send(Message::Tick).unwrap();
        thread::sleep(Duration::from_millis(10000));
    });

    thread::spawn(move || connect(format!("http://{}:8080/v2/events", host), |e| {
        if e.event == Some("status_update_event".to_string()) {
            marathon_tx.send(Message::Update).unwrap();
        }
    }));

    let mut events = Events::with_capacity(1024);
    loop {
        poll.poll(&mut events, None).unwrap();
        for event in events.iter() {
            match event.token() {
                TIMER => match timer_rx.try_recv() {
                    Err(e) => debug!("Error during recv: {}", e),
                    Ok(_) => match autoscale.tick() {
                        Err(e) => debug!("Error during tick: {}", e),
                        Ok(apps) => {
                            log_apps(&apps);
                        }
                    }
                },
                MARATHON => match marathon_rx.try_recv() {
                    Err(e) => debug!("Error during recv: {}", e),
                    Ok(_) => autoscale.update().unwrap(),
                },
                _ => unreachable!(),
            }
        }
    }
}

#[derive(Debug)]
enum Message {
    Update,
    Tick,
}

fn log_apps(apps: &Vec<AppInfo>) {
    info!("----------------------------------------");
    info!("Nb apps: {}", apps.len());
    for app in apps.iter() {
        info!("App: {}", app.app);
        info!("Instances: {}/{}", app.instances, app.max_instances);
        info!("CPU: {}", app.cpu_usage);
        info!("MEM: {}", app.mem_usage);
    }
}
