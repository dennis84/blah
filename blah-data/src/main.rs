#[macro_use] extern crate log;
#[macro_use] extern crate serde_json;
extern crate serde;
extern crate getopts;
extern crate logstash_format;
extern crate futures;
extern crate tokio_core;
extern crate hyper;
extern crate rdkafka;
extern crate r2d2;
extern crate r2d2_postgres;
extern crate postgres;
extern crate chrono;

use std::env;
use std::sync::Arc;

use futures::{future, Future, Stream};

use tokio_core::reactor::{Core, Remote};
use tokio_core::net::TcpListener;

use hyper::{header, StatusCode, Method};
use hyper::server::{Http, Service, Request, Response};

use getopts::Options;

use serde_json::{from_slice, to_string, Value};

use r2d2_postgres::{TlsMode, PostgresConnectionManager};

use rdkafka::config::{ClientConfig, TopicConfig};
use rdkafka::client::EmptyContext;
use rdkafka::producer::{FutureProducer, FutureProducerTopic};

use chrono::prelude::*;

type FutureResponse = Box<Future<Item = Response, Error = hyper::Error>>;

struct DataService {
    pool: r2d2::Pool<PostgresConnectionManager>,
    topic: Arc<FutureProducerTopic<EmptyContext>>,
    remote: Remote,
}

impl Service for DataService {
    type Request = Request;
    type Response = Response;
    type Future = FutureResponse;
    type Error = hyper::Error;

    fn call(&self, req: Self::Request) -> Self::Future {
        let (method, uri, _, _, body) = req.deconstruct();
        let conn = self.pool.get().unwrap();
        let topic_handle = self.topic.clone();
        let remote = self.remote.clone();

        Box::new(body.fold(Vec::new(), |mut bytes, chunk| {
            bytes.extend_from_slice(chunk.as_ref());
            Ok::<_, hyper::Error>(bytes)
        }).and_then(move |bytes| {
            match (method, uri.path()) {
                (Method::Post, p) if p.starts_with("/events/") => {
                    let collection = p.split("/").nth(2).unwrap();
                    match from_slice::<Value>(&bytes) {
                        Ok(props) => {
                            let date = UTC::now();

                            conn.execute("
                                INSERT INTO events (date, collection, props)
                                VALUES ($1, $2, $3)
                            ", &[&date, &collection.clone(), &props]).unwrap();

                            let payload = json!({
                                "date": date.to_rfc3339(),
                                "collection": collection,
                                "props": props,
                            });

                            let fut = topic_handle.send_copy::<String, String>(
                                None, Some(&to_string(&payload).unwrap()), None
                            ).unwrap().and_then(|_| {
                                debug!("Kafka message successfully sent.");
                                Ok(())
                            }).or_else(|e| {
                                error!("Could not send kafka message: {}", e);
                                Ok(())
                            });

                            remote.spawn(move |_| fut);

                            future::ok(Response::new()
                                .with_status(StatusCode::NoContent))
                        },
                        Err(_) => {
                            let resp = Response::new()
                                .with_header(header::ContentType::json())
                                .with_body(r#"{"message": "Bad Request"}"#)
                                .with_status(StatusCode::BadRequest);
                            future::ok(resp)
                        },
                    }
                },
                (Method::Get, "/") => {
                    let resp = Response::new()
                        .with_header(header::ContentType::json())
                        .with_body(r#"{"message": "Healthy"}"#)
                        .with_status(StatusCode::Ok);
                    future::ok(resp)
                },
                _ => {
                    let resp = Response::new()
                        .with_header(header::ContentType::json())
                        .with_body(r#"{"message": "Not Found"}"#)
                        .with_status(StatusCode::NotFound);
                    future::ok(resp)
                },
            }
        }))
    }
}

fn main() {
    logstash_format::new_builder(json!({
        "app": "data",
    })).init().unwrap();

    let args: Vec<String> = env::args().collect();

    let mut opts = Options::new();
    opts.optopt("p", "port", "Set http port", "8080");

    let matches = opts.parse(&args[1..]).unwrap();
    let port = matches.opt_str("port").unwrap_or("8080".to_string());

    let mut core = Core::new().unwrap();
    let handle = core.handle();
    let addr = format!("0.0.0.0:{}", port).parse().unwrap();
    let listener = TcpListener::bind(&addr, &core.handle()).unwrap();

    let mut database_url = "postgres://postgres@localhost".to_string();
    if env::var("DATABASE_URL").is_ok() {
        database_url = env::var("DATABASE_URL").unwrap();
    }

    let config = r2d2::Config::default();
    let manager = PostgresConnectionManager::new(database_url,
                                                 TlsMode::None).unwrap();
    let pool = r2d2::Pool::new(config, manager).unwrap();
    let conn = pool.get().unwrap();
    conn.execute("CREATE TABLE IF NOT EXISTS events (
                    id SERIAL PRIMARY KEY NOT NULL,
                    collection TEXT NOT NULL,
                    props jsonb NOT NULL,
                    date TIMESTAMPTZ(6) NOT NULL DEFAULT statement_timestamp()
                  );", &[]).unwrap();

    let mut kafka_url = "localhost:9092".to_string();
    if env::var("KAFKA_URL").is_ok() {
        kafka_url = env::var("KAFKA_URL").unwrap();
    }

    let producer = ClientConfig::new()
        .set("bootstrap.servers", &kafka_url)
        .create::<FutureProducer<_>>()
        .expect("Producer creation error");

    let topic_config = TopicConfig::new()
        .set("produce.offset.report", "true")
        .finalize();

    producer.start();

    let topic = Arc::new(producer.get_topic("events", &topic_config)
        .expect("Topic creation error"));
    
    let work = listener.incoming().for_each(move |(socket, addr)| {
        let service = DataService {
            pool: pool.clone(),
            topic: topic.clone(),
            remote: handle.remote().clone(),
        };

        Http::new().bind_connection(&handle, socket, addr, service);
        Ok(())
    });

    info!("Listening on http://{}", addr);
    core.run(work).unwrap();
}
