#[macro_use] extern crate nickel;

use nickel::{Nickel, HttpRouter, StaticFilesHandler};

fn main() {
    let mut server = Nickel::new();
    server.get("/healthcheck", middleware!(""));
    server.utilize(StaticFilesHandler::new("assets"));
    server.listen("0.0.0.0:8002");
}
