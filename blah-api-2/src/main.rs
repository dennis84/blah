extern crate hyper;
extern crate rustc_serialize;
extern crate uuid;
extern crate kafka;
extern crate byteorder;
extern crate protobuf;

use hyper::{Get, Post};
use hyper::server::{Server, Request, Response};
use hyper::header::{ContentType};
use hyper::uri::RequestUri::AbsolutePath;
use rustc_serialize::json::{self, Json};
use uuid::Uuid;
use std::io::Read;
use kafka::producer::{Producer, Record};
mod hdfs;
mod proto;

#[derive(RustcEncodable)]
struct Event {
    id: Uuid,
    collection: String,
    props: Json
}

fn echo(mut req: Request, mut res: Response) {
    let mut producer =
        Producer::from_hosts(vec!("localhost:9092".to_owned()))
            .with_ack_timeout(1000)
            .with_required_acks(1)
            .create()
            .unwrap();

    match req.uri.clone() {
        AbsolutePath(ref path) => match (&req.method, &path[..]) {
            (&Post, path) if path.starts_with("/events/") => {
                let (_, collection) = path.split_at(8);
				let mut body = String::new();
                req.read_to_string(&mut body).unwrap();
                let event = Event {
                    id: Uuid::new_v4(),
                    collection: collection.to_string(),
                    props: Json::from_str(&body).unwrap()
                };

                let encoded = json::encode(&event).unwrap();
                producer.send(&Record::from_value("test", encoded.as_bytes())).unwrap();

                res.headers_mut().set(ContentType::json());
                res.send(b"{\"message\": \"Event successfully created.\"}").unwrap();
                return;
            },
            (&Get, "/") => {
                res.headers_mut().set(ContentType::json());
                res.send(b"{\"message\": \"Ok\"}").unwrap();
                return;
            },
            _ => {
                *res.status_mut() = hyper::NotFound;
                return;
            }
        },
        _ => {
            return;
        }
    };
}

fn main() {
    Server::http("0.0.0.0:8000")
        .unwrap()
        .handle(echo)
        .unwrap();
}
