use std::env;
use log::{LogRecord, LogLevelFilter};
use env_logger::LogBuilder;
use chrono;

pub fn new_logger() {
    let format = |record: &LogRecord| {
        let string = vec![
            format!("\"@timestamp\":\"{:?}\"", chrono::Local::now()),
            format!("\"@version\":\"{}\"", "1"),
            format!("\"message\":\"{}\"", record.args()),
            format!("\"level\":\"{}\"", record.level())
        ].join(",");

        format!("{{{}}}", string)
    };

    let mut builder = LogBuilder::new();
    builder.format(format).filter(None, LogLevelFilter::Info);

    if env::var("RUST_LOG").is_ok() {
        builder.parse(&env::var("RUST_LOG").unwrap());
    }

    builder.init().unwrap();
}
