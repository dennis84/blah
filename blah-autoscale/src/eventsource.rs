use std::io;
use std::thread;
use std::sync::mpsc::channel;
use curl::easy::{Easy, List};
use tokio_core::reactor::Core;
use tokio_curl::{Session};

#[derive(Debug)]
pub struct Event {
    pub data: String,
    pub event: Option<String>,
    pub id: Option<String>,
    pub retry: Option<u64>,
}

pub fn connect<F>(url: &str, fun: F) -> io::Result<()> 
        where F: FnMut(Event) -> () {
    let (tx, rx) = channel();

    let mut req = Easy::new();
    try!(req.get(true));
    try!(req.url(url));

    let mut list = List::new();
    try!(list.append("Accept: text/event-stream"));
    try!(req.http_headers(list));

    thread::spawn(move || {
        let mut evloop = Core::new().unwrap();
        let handle = evloop.handle();
        let session = Session::new(handle);

        let tx2 = tx.clone();
        req.write_function(move |data| {
            tx2.send(String::from_utf8_lossy(&data).into_owned()).unwrap();
            Ok(data.len())
        }).unwrap();

        let future = session.perform(req);
        evloop.run(future).unwrap();
    });

    let mut callback = fun;

    loop {
        match rx.recv() {
            Err(e) => debug!("Error during recv: {}", e),
            Ok(message) => callback(parse_message(&message))
        }
    }
}

fn parse_message(message: &str) -> Event {
    let mut event = Event {
        id: None,
        event: None,
        data: "".to_string(),
        retry: None,
    };

    for line in message.split("\r\n") {
        match line.find(":") {
            Some(pos) => {
                let (field, value) = line.split_at(pos);
                let value = &value[2..];
                match field {
                    "event" => {
                        event.event = Some(value.to_string());
                    }
                    "data" => {
                        event.data.push_str(value);
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
            None => {}
        }
    }

    event
}

#[test]
fn test_parse_message() {
    let message = "\
        event: foo\r\n\
        data: bar\r\n\r\n\
    ";
    
    let event = parse_message(message);
    assert!(Some("foo".to_string()) == event.event);
    assert!("bar" == event.data);

    let message = "\
        event: foo\r\n\
        data: {\"a\": 0}\r\n\r\n\
    ";
    
    let event = parse_message(message);
    assert!(Some("foo".to_string()) == event.event);
    assert!(r#"{"a": 0}"# == event.data);

    let message = "";
    let event = parse_message(message);
    assert!(None == event.event);
    assert!("" == event.data);
}

#[test]
#[ignore]
fn test_example() {
    connect("http://172.17.42.1:8080/v2/events", |e| {
        println!("{:?}", e);
    });
}
