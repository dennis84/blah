extern crate hyper;

use hyper::server::{Request, Response};

static PHRASE: &'static [u8] = b"Hello World!";

fn hello(_: Request, res: Response) {
    res.send(PHRASE).unwrap();
}

fn main() {
    let _ = hyper::Server::http("0.0.0.0:8000").unwrap().handle(hello);
    println!("Listening on http://0.0.0.0:8000");
}
