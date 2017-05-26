use std::io::Read;
use std::sync::{Arc, Mutex};
use std::time::Duration;
use std::collections::HashMap;

use futures::{Future, Stream};

use tokio_core::reactor::Core;
use tokio_timer::Timer;
use tokio_curl::Session;

use curl::easy::Easy;

use rdkafka::config::{ClientConfig, TopicConfig};
use rdkafka::consumer::Consumer;
use rdkafka::consumer::stream_consumer::StreamConsumer;

use serde_json::{self, Value};

use chrono::{DateTime, UTC};
use chrono::prelude::*;

use uuid::{Uuid, NAMESPACE_DNS};

pub fn start(app: &::Application) {
    let mut core = Core::new().unwrap();

    let mut consumer = ClientConfig::new()
        .set("group.id", "blah-collection-processing-0")
        .set("bootstrap.servers", &app.kafka_url)
        .set("enable.partition.eof", "false")
        .set("session.timeout.ms", "6000")
        .set("enable.auto.commit", "true")
        .set_default_topic_config(TopicConfig::new()
            .set("auto.offset.reset", "largest")
            .finalize())
        .create::<StreamConsumer<_>>()
        .expect("Consumer creation failed");

    consumer.subscribe(&vec!["events"])
        .expect("Can't subscribe to specified topics");

    let events = Arc::new(Mutex::new(HashMap::new()));
    let events_write = events.clone();

    let message_stream = consumer
        .start()
        .filter_map(|message| match message {
            Ok(m) => Some(m),
            Err(e) => {
                warn!("Error while receiving from Kafka: {:?}", e);
                None
            }
        })
        .filter_map(|message| match message.payload_view::<str>() {
            Some(Ok(payload)) => {
                match serde_json::from_str::<Value>(&payload) {
                    Ok(json) => {
                        let name = json["collection"].as_str().unwrap();
                        let date = json["date"].as_str().unwrap();
                        let date: DateTime<UTC> = date.parse().unwrap();
                        let date = date.with_nanosecond(0).unwrap();

                        Some(::Collection {
                            date: date,
                            name: name.to_string(),
                            count: 0,
                        })
                    },
                    Err(_) => None,
                }
            },
            _ => None,
        })
        .for_each(move |collection| {
            let mut map = events_write.lock().unwrap();
            let id = collection.date.to_rfc3339() + &collection.name;
            let id = Uuid::new_v5(&NAMESPACE_DNS, &id);
            let obj = map.entry(id).or_insert(collection);
            obj.count += 1;

            Ok(())
        });

    let handle = core.handle();
    handle.spawn(message_stream);

    let timer = Timer::default();
    let interval = Duration::from_millis(app.streaming_batch_interval);
    let interval = timer.interval(interval);
    let stream = interval
        .map_err(|_| ())
        .for_each(|_| {
            let mut map = events.lock().unwrap();

            if map.is_empty() {
                return Ok(());
            }

            let mut kafka_data = Vec::new();
            let mut elastic_data = Vec::new();

            for (key, value) in map.iter_mut() {
                kafka_data.push(serde_json::to_string(&value).unwrap());
                elastic_data.push(serde_json::to_string(&json!({
                    "update": {
                        "_id": key.to_string(),
                        "_index": "collection",
                        "_type": "count",
                    }
                })).unwrap());

                elastic_data.push(serde_json::to_string(&json!({
                    "script": {
                        "inline": "ctx._source.count += count",
                        "lang": "groovy",
                        "params": {"count": value.count}
                    },
                    "upsert": value
                })).unwrap());
            }
    
            let elastic_data = elastic_data.join("\n") + "\n";
            let session = Session::new(handle.clone());
            let mut req = Easy::new();

            req.url(&format!("{}/_bulk", app.elastic_url)).unwrap();
            req.post(true).unwrap();

            req.post_field_size(elastic_data.as_bytes().len() as u64).unwrap();
            req.read_function(move |buf| {
                let mut data = elastic_data.as_bytes();
                Ok(data.read(buf).unwrap_or(0))
            }).unwrap();

            let elastic_fut = session.perform(req).and_then(|_| {
                debug!("Elastic request successfully sent.");
                Ok(())
            }).or_else(|e| {
                error!("Could not send elastic request: {}", e);
                Ok(())
            });

            handle.spawn(elastic_fut);

            let kafka_data = serde_json::to_string(&kafka_data).unwrap();
            let kafka_data = format!("collection@{}", kafka_data);

            let fut = app.producer.send_copy::<String, String>(
                "trainings", None, Some(&kafka_data), None, None
            ).unwrap().and_then(|_| {
                debug!("Kafka message successfully sent.");
                Ok(())
            }).or_else(|e| {
                error!("Could not send kafka message: {}", e);
                Ok(())
            });

            handle.spawn(fut);
            map.clear();

            Ok(())
        });

    core.run(stream).unwrap();
}
