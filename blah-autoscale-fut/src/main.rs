extern crate curl;
extern crate futures;
extern crate tokio_core;
extern crate tokio_curl;
extern crate rustc_serialize;

mod service;

use tokio_core::reactor::Core;
use service::{Service};

fn main() {
    let host = "172.17.42.1";
    let mut evloop = Core::new().unwrap();
    let mut service = Service::new(
        host.to_string(),
        80.0,
        80.0,
        1.5,
        10
    ).unwrap();

    let fut = service.get_apps(evloop.handle());
    match evloop.run(fut) {
        Ok(apps) => {
            for id in apps {
                let fut = service.get_app(evloop.handle(), &id);
                match evloop.run(fut) {
                    Ok(app) => println!("{:?}", app),
                    Err(_) => println!("err"),
                }
            }
        }
        Err(_) => println!("fail"),
    }

    let fut = service.get_slaves(evloop.handle());
    match evloop.run(fut) {
        Ok(x) => println!("{:?}", x),
        Err(_) => println!("fail"),
    }
}
