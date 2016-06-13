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

use std::io::prelude::*;
use std::io::{self, BufWriter};
use std::borrow::Borrow;
use mio::tcp::{TcpStream};
use hyper::http::h1::parse_response;
use hyper::buffer::BufReader;
use mio::*;
use url::Url;

#[derive(Debug)]
pub struct Event {
    pub data: String,
    pub event: Option<String>,
    pub id: Option<String>,
    pub retry: Option<u64>,
}

struct EventSourceHandler<F>
    where F: FnMut(Event) -> () {
    reader: BufReader<TcpStream>,
    initialized: bool,
    callback: F,
}

impl<F> EventSourceHandler<F>
    where F: FnMut(Event) -> () {

    fn read(&mut self) {
        if false == self.initialized {
            parse_response(&mut self.reader).unwrap();
            self.initialized = true;
        }

        let mut event = Event {
            id: None,
            event: None,
            data: "".to_string(),
            retry: None,
        };

        let mut line = String::new();
        while self.reader.read_line(&mut line).unwrap_or(0) > 0 {
            self.parse_line(&line, &mut event);
            line.clear();
        }

        if ! event.data.is_empty() {
            let fun = &mut self.callback;
            fun(event);
        }
    }

    fn parse_line(&self, line: &str, event: &mut Event) {
        let line = if line.ends_with("\r\n") {
            &line[0..line.len()-2]
        } else if line.ends_with('\n') {
            &line[0..line.len()-1]
        } else {
            line
        };

        let (field, value) = if let Some(pos) = line.find(':') {
            let (f, v) = line.split_at(pos);
            let v = if v.starts_with(": ") { &v[2..] } else { &v[1..] };
            (f, v)
        } else {
            return;
        };

        match field {
            "event" => {
                event.event = Some(value.to_string());
            }
            "data" => {
                event.data.push_str(value);
                event.data.push('\n');
            }
            "id" => {
                event.id = Some(value.to_string());
            }
            "retry" => {
                event.retry = value.parse().ok();
            }
            _ => ()
        }
    }
}

impl<F> Handler for EventSourceHandler<F>
    where F: FnMut(Event) -> () {
    type Timeout = ();
    type Message = ();

    fn ready(&mut self, _: &mut EventLoop<Self>,
             token: Token, _: EventSet) {
        assert_eq!(token, Token(1));
        self.read();
    }
}

pub fn connect<F,U>(url: U, fun: F) -> io::Result<()> 
    where F: FnMut(Event) -> (),
          U: Borrow<str> {
    let url = Url::parse(url.borrow()).unwrap();
    let addr = match (url.host(), url.port()) {
        (Some(host), Some(port)) => format!("{}:{}", host, port),
        _ => panic!("Invalid URL"),
    };

    let stream = try!(TcpStream::connect(&addr.parse().unwrap()));
    let writer = try!(stream.try_clone());
    let mut writer = BufWriter::new(writer);
    try!(writer.write(&format!(
        "GET {} HTTP/1.1\r\n", url.path()).into_bytes()[..]));
    try!(writer.write(&format!(
        "HOST: {}\r\n", addr).into_bytes()[..]));
    try!(writer.write(b"accept: text/event-stream\r\n\r\n"));
    try!(writer.flush());

    let mut event_loop = try!(EventLoop::new());
    try!(event_loop.register(&stream, Token(1),
                             EventSet::readable(),
                             PollOpt::edge()));
    try!(event_loop.run(&mut EventSourceHandler {
        reader: BufReader::new(stream),
        initialized: false,
        callback: fun,
    }));

    Ok(())
}
