#[macro_use] extern crate log;
#[macro_use] extern crate serde_json;
#[macro_use] extern crate serde_derive;
extern crate serde;
extern crate getopts;
extern crate futures;
extern crate tokio_core;
extern crate tokio_proto;
extern crate tokio_service;
extern crate rdkafka;
extern crate logstash_format;
extern crate minihttp;

use std::env;
use std::{io, thread};
use std::sync::{Arc, Mutex};

use getopts::Options;

use futures::{future, Future, Stream, Sink};
use futures::sync::mpsc::Sender;

use tokio_core::reactor::Core;
use tokio_proto::streaming::{Body, Message};
use tokio_proto::TcpServer;
use tokio_service::Service;

use minihttp::{Request, Response, Http};

use rdkafka::config::{ClientConfig, TopicConfig};
use rdkafka::consumer::Consumer;
use rdkafka::consumer::stream_consumer::StreamConsumer;

type BodySender = Sender<Result<String, io::Error>>;

struct EventService {
    connections: Arc<Mutex<Vec<BodySender>>>,
}

impl Service for EventService {
    type Request = Message<Request, Body<String, Self::Error>>;
    type Response = Message<Response, Body<String, Self::Error>>;
    type Future = Box<Future<Item = Self::Response, Error = Self::Error>>;
    type Error = io::Error;

    fn call(&self, req: Self::Request) -> Self::Future {
        match (req.method(), req.path()) {
            ("OPTIONS", _) => {
                let mut resp = Response::new();
                resp.header("Access-Control-Allow-Credentials", "true");
                resp.header("Access-Control-Allow-Headers", "content-type");
                resp.header("Access-Control-Allow-Methods", "OPTIONS, GET, POST, PUT, DELETE");
                resp.header("Access-Control-Allow-Origin", "*");
                future::ok(Message::WithoutBody(resp)).boxed()
            },
            ("GET", "/events") => {
                let (tx, rx) = Body::pair();
                self.connections.lock().unwrap().push(tx);
                let mut resp = Response::new();
                resp.header("Access-Control-Allow-Credentials", "true");
                resp.header("Access-Control-Allow-Headers", "content-type");
                resp.header("Access-Control-Allow-Origin", "*");
                resp.header("Content-Type", "text/event-stream");
                resp.header("Cache-Control", "no-cache");
                future::ok(Message::WithBody(resp, rx)).boxed()
            },
            ("GET", "/") => {
                let mut resp = Response::new();
                resp.header("Access-Control-Allow-Credentials", "true");
                resp.header("Access-Control-Allow-Headers", "content-type");
                resp.header("Access-Control-Allow-Origin", "*");
                resp.header("Content-Type", "application/json");
                resp.body(r#"{"message": "Healthy"}"#);
                future::ok(Message::WithoutBody(resp)).boxed()
            }
            _ => {
                let mut resp = Response::new();
                resp.header("Access-Control-Allow-Credentials", "true");
                resp.header("Access-Control-Allow-Headers", "content-type");
                resp.header("Access-Control-Allow-Origin", "*");
                resp.header("Content-Type", "application/json");
                resp.status_code(404, "Not Found");
                resp.body(r#"{"message": "Not Found"}"#);
                future::ok(Message::WithoutBody(resp)).boxed()
            }
        }
    }
}

fn main() {
    logstash_format::new_builder(json!({
        "app": "events",
    })).init().unwrap();

    let args: Vec<String> = env::args().collect();

    let mut opts = Options::new();
    opts.optopt("p", "port", "Set http port", "8080");

    let matches = opts.parse(&args[1..]).unwrap();
    let port = matches.opt_str("port").unwrap_or("8080".to_string());

    let connections: Arc<Mutex<Vec<BodySender>>> = Arc::new(Mutex::new(Vec::new()));
    let connections_inner = connections.clone();

    let mut kafka_url = "localhost:9092".to_string();
    if env::var("KAFKA_URL").is_ok() {
        kafka_url = env::var("KAFKA_URL").unwrap();
    }

    thread::spawn(move || {
        let mut core = Core::new().unwrap();
        let mut consumer = ClientConfig::new()
            .set("group.id", "blah-events-0")
            .set("bootstrap.servers", &kafka_url)
            .set("enable.partition.eof", "false")
            .set("session.timeout.ms", "6000")
            .set("enable.auto.commit", "true")
            .set_default_topic_config(TopicConfig::new()
                .set("auto.offset.reset", "largest")
                .finalize())
            .create::<StreamConsumer<_>>()
            .expect("Consumer creation failed");

        consumer.subscribe(&vec!["trainings"])
            .expect("Can't subscribe to specified topics");

        let message_stream = consumer.start();

        let stream = message_stream
            .filter_map(|message| match message {
                Ok(m) => Some(m),
                Err(e) => {
                    warn!("Error while receiving from Kafka: {:?}", e);
                    None
                }
            })
            .for_each(|message| match message.payload_view::<str>() {
                Some(Ok(s)) => {
                    let data = format!("data: {}\n\n", s);
                    let mut conns = connections.lock().unwrap();

                    info!("Send data to {} connections.", conns.len());
                    *conns = conns.iter_mut().filter_map(|tx| {
                        match tx.send(Ok(data.to_string())).wait() {
                            Ok(_) => Some(tx.to_owned()),
                            Err(_) => None,
                        }
                    }).collect::<Vec<_>>();
                    Ok(())
                },
                _ => Ok(()),
            });

        core.run(stream).unwrap();
    });

    let addr = format!("0.0.0.0:{}", port).parse().unwrap();
    let srv = TcpServer::new(Http, addr);

    info!("Listening on http://{}", addr);
    srv.serve(move || Ok(EventService {
        connections: connections_inner.clone(),
    }));
}
