//! Usage
//! -----
//!
//! ```
//! mod eventsource;
//! use eventsource::{connect};
//!
//! let client = connect("http://172.17.42.1:8080/v2/events", |event| {
//!     println!("{:?}", event);
//! }).unwrap();
//! ```

use std::io::{self, Write, Read};
use std::net::{SocketAddr, ToSocketAddrs};
use std::borrow::Borrow;
use url::Url;
use tokio_core::net::TcpStream;
use tokio_core::reactor::Core;
use tokio_core;
use tokio_service::{Service};
use futures::*;

#[derive(Debug)]
pub struct Event {
    pub data: String,
    pub event: Option<String>,
    pub id: Option<String>,
    pub retry: Option<u64>,
}

struct Client {}

impl Service for Client {
    type Request = String;
    type Response = String;
    type Error = io::Error;
    type Future = Box<Future<Item = Self::Response, Error = io::Error>>;

    fn call(&self, req: String) -> Self::Future {
        // Create the HTTP response
        let resp = "test".to_string();
        ::futures::finished(resp).boxed()
    }
}

pub fn connect<F,U>(url: U, fun: F) -> io::Result<()> 
    where F: FnMut(Event) -> (),
          U: Borrow<str> {
    let url = try!(Url::parse(url.borrow()).or_else(|e| {
        Err(io::Error::new(io::ErrorKind::Other, e))
    }));
    let addr = try!(parse_url(&url));
    let mut evloop = Core::new().unwrap();
    let handle = evloop.handle();

    // let socket = TcpStream::connect(&addr, &handle);

    // let mut request = socket.and_then(move |s| {
    //     let body = format!("\
    //         GET {} HTTP/1.0\r\n\
    //         Host: {}\r\n\
    //         Accept: text/event-stream\r\n\r\n\
    //     ", url.path(), addr);
    //     tokio_core::io::write_all(s, body)
    // });
    
    Ok(())
}

fn parse_url(url: &Url) -> io::Result<SocketAddr> {
    let host = url.host_str();

    if host.is_none() {
        return Err(io::Error::new(io::ErrorKind::Other, "Invalid URL"))
    }

    let port = url.port_or_known_default().unwrap_or(80);
    let addrs = try!((&host.unwrap()[..], port).to_socket_addrs());
    let addrs = addrs.collect::<Vec<SocketAddr>>();

    match addrs.first() {
        Some(_) => Ok(addrs[0]),
        None => Err(io::Error::new(io::ErrorKind::Other, "Parse error")),
    }
}

// #[test]
// fn test_parse_url() {
//     let url = Url::parse("http://google.com").unwrap();
//     let url = parse_url(&url);
//     assert!(url.is_ok());
//     assert!(80 == url.unwrap().port());
//     let url = Url::parse("http://8.8.8.8:8000").unwrap();
//     let url = parse_url(&url);
//     assert!(url.is_ok());
//     assert!(8000 == url.unwrap().port());
// }

#[test]
fn test_example() {
    let host = "172.17.42.1";
    connect(format!("http://{}:8080/v2/events", host), |e| {
        println!("{:?}", e);
    });
}
