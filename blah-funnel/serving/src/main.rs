#[macro_use] extern crate log;
#[macro_use] extern crate serde_json;
#[macro_use] extern crate serde_derive;
#[macro_use] extern crate mime;
extern crate serde;
extern crate getopts;
extern crate logstash_format;
extern crate futures;
extern crate tokio_core;
extern crate hyper;
extern crate unicase;
extern crate hyper_static;

mod repo;

use std::env;

use futures::{future, Future, BoxFuture, Stream};
use tokio_core::reactor::Core;
use tokio_core::net::TcpListener;

use hyper::{header, StatusCode, Client, Method};
use hyper::server::{Http, Service, Request, Response};

use unicase::UniCase;

use getopts::Options;

use repo::{FunnelQuery, FunnelRepo};

type FutureResponse = Box<Future<Item = Response, Error = hyper::Error>>;

#[derive(Serialize, Debug)]
struct Message {
    message: String,
}

struct FunnelService {
    repo: FunnelRepo,
}

impl FunnelService {
    fn list(&self, req: Request) -> FutureResponse {
        let repo = self.repo.clone();
        Box::new(Self::deserialize::<FunnelQuery>(req)
            .and_then(move |result| match result {
                Ok(query) => {
                    Box::new(repo.search(query).map(|resp| {
                        Self::new_response(resp)
                    })) as FutureResponse
                },
                Err(_) => {
                    let resp = Self::new_response(Message {
                        message: "Invalid JSON body".to_string(),
                    }).with_status(StatusCode::BadRequest);
                    Box::new(future::ok(resp))
                },
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

    fn deserialize<T>(req: Request)
            -> BoxFuture<Result<T, serde_json::Error>, hyper::Error>
            where T: serde::de::Deserialize + Send + 'static {
        req.body().fold(Vec::new(), |mut bytes, chunk| {
            bytes.extend_from_slice(chunk.as_ref());
            Ok::<_, hyper::Error>(bytes)
        }).and_then(|bytes| {
            future::ok(serde_json::from_slice(bytes.as_slice()))
        }).boxed()
    }
}

impl Service for FunnelService {
    type Request = Request;
    type Response = Response;
    type Future = FutureResponse;
    type Error = hyper::Error;

    fn call(&self, req: Self::Request) -> Self::Future {
        match (req.method(), req.path()) {
            (&Method::Post, "/funnels") => self.list(req),
            (&Method::Get, _) if req.path().starts_with("/js")
                => Box::new(hyper_static::from_dir("dist", req)),
            (&Method::Get, "/") => Self::health(),
            _ => Self::not_found(),
        }
    }
}

fn main() {
    logstash_format::new_builder(json!({
        "app": "funnel",
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
    let mut elastic_url = "http://localhost:9200".to_string();
    if env::var("ELASTICSEARCH_URL").is_ok() {
        elastic_url = env::var("ELASTICSEARCH_URL").unwrap();
    }

    let work = listener.incoming().for_each(move |(socket, addr)| {
        let repo = FunnelRepo {
            client: client.clone(),
            elastic_url: elastic_url.clone(),
        };

        let service = FunnelService { repo: repo };
        Http::new().bind_connection(&handle, socket, addr, service);
        Ok(())
    });

    info!("Listening on http://{}", addr);
    core.run(work).unwrap();
}
