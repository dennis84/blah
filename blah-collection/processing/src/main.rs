#[macro_use] extern crate log;
#[macro_use] extern crate serde_json;
#[macro_use] extern crate serde_derive;
extern crate serde;
extern crate futures;
extern crate curl;
extern crate tokio_core;
extern crate tokio_timer;
extern crate tokio_curl;
extern crate rdkafka;
extern crate postgres;
extern crate r2d2;
extern crate r2d2_postgres;
extern crate fallible_iterator;
extern crate uuid;
extern crate chrono;
extern crate logstash_format;

mod streaming;
mod batch;

use std::env;

use chrono::{DateTime, UTC};

use rdkafka::producer::FutureProducer;
use rdkafka::config::{ClientConfig, TopicConfig};
use rdkafka::client::EmptyContext;

pub struct Application {
    elastic_url: String,
    postgres_url: String,
    kafka_url: String,
    streaming_batch_interval: u64,
    producer: FutureProducer<EmptyContext>,
}

#[derive(Serialize, Clone)]
struct Collection {
    name: String,
    date: DateTime<UTC>,
    count: u64,
}

fn main() {
    logstash_format::new_builder(json!({
        "app": "collection-processing",
    })).init().unwrap();

    let mut elastic_url = "localhost:9200".to_string();
    if env::var("ELASTICSEARCH_URL").is_ok() {
        elastic_url = env::var("ELASTICSEARCH_URL").unwrap();
    }

    let mut kafka_url = "localhost:9092".to_string();
    if env::var("KAFKA_URL").is_ok() {
        kafka_url = env::var("KAFKA_URL").unwrap();
    }

    let mut postgres_url = "postgres://postgres@localhost".to_string();
    if env::var("POSTGRES_URL").is_ok() {
        postgres_url = env::var("POSTGRES_URL").unwrap();
    }
    
    let mut streaming_batch_interval = 5000;
    if env::var("STREAMING_BATCH_INTERVAL").is_ok() {
        streaming_batch_interval = env::var("STREAMING_BATCH_INTERVAL")
            .unwrap().parse::<u64>().unwrap();
    }

    let producer = ClientConfig::new()
        .set("bootstrap.servers", &kafka_url)
        .set_default_topic_config(TopicConfig::new()
            .set("produce.offset.report", "true")
            .finalize())
        .create::<FutureProducer<_>>()
        .expect("Producer creation error");

    let app = Application {
        elastic_url: elastic_url,
        postgres_url: postgres_url,
        kafka_url: kafka_url,
        streaming_batch_interval: streaming_batch_interval,
        producer: producer,
    };

    let command = env::args().nth(1).unwrap_or("".to_string());

    match command.as_ref() {
        "streaming" => {
            info!("Start streaming ...");
            streaming::start(&app);
        },
        "batch" => {
            batch::start(&app);
        },
        value => {
            error!("Error: No such command: {}", value);
        },
    }
}
