use std::env;
use log::{LogRecord, LogLevelFilter};
use env_logger::LogBuilder;
use rustc_serialize::json::{encode};
use chrono::Local;

pub fn new_logger() {
    let format = |record: &LogRecord| {
        let message = record.args().to_string();
        let string = vec![
            format!("\"@timestamp\":\"{:?}\"", Local::now()),
            format!("\"@version\":\"{}\"", "1"),
            format!("\"message\":{}", encode(&message).unwrap()),
            format!("\"level\":\"{}\"", record.level()),
            format!("\"app\":\"{}\"", "autoscale"),
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

// #[test]
// fn test_log() {
//     new_logger();
//     error!("{}", r#"{"foo": 42}"#);
//     error!("bar");
// }
