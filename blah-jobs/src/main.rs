#[macro_use] extern crate log;
#[macro_use] extern crate serde_json;
#[macro_use] extern crate serde_derive;
#[macro_use] extern crate mime;
extern crate serde;
extern crate logstash_format;
extern crate futures;
extern crate tokio_core;
extern crate hyper;
extern crate unicase;
extern crate getopts;
extern crate hyper_static;

mod chronos;

use std::env;

use futures::{future, Future, Stream};
use tokio_core::reactor::Core;
use tokio_core::net::TcpListener;

use hyper::{header, StatusCode, Client, Method};
use hyper::server::{Http, Service, Request, Response};

use unicase::UniCase;

use getopts::Options;

use chronos::{JobRepo};

type FutureResponse = Box<Future<Item = Response, Error = hyper::Error>>;

#[derive(Serialize, Debug)]
struct Message {
    message: String,
}

struct JobsService {
    job_repo: JobRepo,
}

impl JobsService {
    fn list(&self) -> FutureResponse {
        Box::new(self.job_repo.list().map(|jobs| {
            Self::new_response(jobs)
        }))
    }

    fn run(&self, name: &str) -> FutureResponse {
        Box::new(self.job_repo.run(name).map(|message| {
            Self::new_response(Message {
                message: message.message,
            }).with_status(message.status)
        }))
    }

    fn stop(&self, name: &str) -> FutureResponse {
        Box::new(self.job_repo.stop(name).map(|message| {
            Self::new_response(Message {
                message: message.message,
            }).with_status(message.status)
        }))
    }

    fn health() -> FutureResponse {
        let resp = Self::new_response(Message {
            message: "Healthy".to_string(),
        });
        Box::new(future::ok(resp))
    }

    fn not_found() -> FutureResponse {
        let resp = Self::new_response(Message {
            message: "Not Found".to_string(),
        }).with_status(StatusCode::NotFound);
        Box::new(future::ok(resp))
    }

    fn new_response<T>(body: T) -> Response
            where T: serde::ser::Serialize {
        let body = serde_json::to_string(&body).unwrap();
        Response::new()
            .with_header(header::AccessControlAllowOrigin::Any)
            .with_header(header::AccessControlAllowCredentials)
            .with_header(header::ContentType::json())
            .with_header(header::AccessControlAllowHeaders(vec![
                UniCase("content-type".to_string()),
            ]))
            .with_body(body)
    }
}

impl Service for JobsService {
    type Request = Request;
    type Response = Response;
    type Future = FutureResponse;
    type Error = hyper::Error;

    fn call(&self, req: Self::Request) -> Self::Future {
        match (req.method(), req.path()) {
            (&Method::Get, "/jobs") => self.list(),
            (&Method::Put, _) if req.path().starts_with("/jobs/") => {
                let name = req.path().split("/").nth(2).unwrap();
                self.run(name)
            },
            (&Method::Delete, _) if req.path().starts_with("/jobs/") => {
                let name = req.path().split("/").nth(2).unwrap();
                self.stop(name)
            },
            (&Method::Get, _) if req.path().starts_with("/js") => {
                Box::new(hyper_static::from_dir("dist", req))
            },
            (&Method::Get, "/") => Self::health(),
            _ => Self::not_found(),
        }
    }
}

fn main() {
    logstash_format::new_builder(json!({
        "app": "jobs",
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

    let client = Client::new(&handle.clone());
    let mut chronos_url = "http://localhost:8081".to_string();
    if env::var("CHRONOS_URL").is_ok() {
        chronos_url = env::var("CHRONOS_URL").unwrap();
    }

    let work = listener.incoming().for_each(move |(socket, addr)| {
        let job_repo = JobRepo {
            client: client.clone(),
            chronos_url: chronos_url.clone(),
        };

        let service = JobsService {
            job_repo: job_repo,
        };

        Http::new().bind_connection(&handle, socket, addr, service);
        Ok(())
    });

    info!("Listening on http://{}", addr);
    core.run(work).unwrap();
}
