use std::io::Read;
use std::collections::HashMap;

use futures::Future;

use tokio_core::reactor::Core;
use tokio_curl::Session;

use curl::easy::Easy;

use r2d2_postgres::{TlsMode, PostgresConnectionManager};
use r2d2::{Config, Pool};

use fallible_iterator::FallibleIterator;

use chrono::{DateTime, UTC};
use chrono::prelude::*;

use uuid::{Uuid, NAMESPACE_DNS};

use serde_json;

pub fn start(app: &::Application) {
    let mut core = Core::new().unwrap();

    let config = Config::default();
    let manager = PostgresConnectionManager::new(app.postgres_url.clone(),
                                                 TlsMode::None).unwrap();
    let pool = Pool::new(config, manager).unwrap();
    let conn = pool.get().unwrap();

    let trans = conn.transaction().unwrap();
    let stmt = conn.prepare("SELECT * FROM events").unwrap();
    let mut data = HashMap::new();

    for row_result in stmt.lazy_query(&trans, &[], 10).unwrap().iterator() {
        let row = row_result.unwrap();
        let maybe_date = row.get_opt::<_, DateTime<UTC>>("date");
        let maybe_name = row.get_opt::<_, String>("collection");

        match (maybe_name, maybe_date) {
            (Some(Ok(name)), Some(Ok(date))) => {
                let date = date.with_nanosecond(0).unwrap();
                let id = date.to_rfc3339() + &name;
                let id = Uuid::new_v5(&NAMESPACE_DNS, &id);

                let obj = data.entry(id).or_insert(::Collection {
                    name: name,
                    date: date,
                    count: 0,
                });

                obj.count += 1;
            },
            _ => (),
        }
    }

    let mut elastic_data = Vec::new();

    for (key, value) in data {
        elastic_data.push(serde_json::to_string(&json!({
            "update": {
                "_id": key.to_string(),
                "_index": "collection",
                "_type": "count",
            }
        })).unwrap());

        elastic_data.push(serde_json::to_string(&json!({
            "doc": value,
            "doc_as_upsert": true,
        })).unwrap());
    }

    let elastic_data = elastic_data.join("\n") + "\n";
    let session = Session::new(core.handle());

    let mut req = Easy::new();
    req.url(&format!("{}/_bulk", app.elastic_url)).unwrap();
    req.post(true).unwrap();

    req.post_field_size(elastic_data.as_bytes().len() as u64).unwrap();
    req.read_function(move |buf| {
        let mut data = elastic_data.as_bytes();
        Ok(data.read(buf).unwrap_or(0))
    }).unwrap();

    let elastic_fut = session.perform(req);

    let kafka_data = "collection@[]".to_string();
    let kafka_fut = app.producer.send_copy::<String, String>(
        "trainings", None, Some(&kafka_data), None, None
    ).unwrap().and_then(|_| {
        debug!("Kafka message successfully sent.");
        Ok(())
    }).or_else(|e| {
        error!("Could not send kafka message: {}", e);
        Ok(())
    });

    let kafka_handle = core.handle();
    kafka_handle.spawn(kafka_fut);

    match core.run(elastic_fut) {
        Ok(_) => info!("Batch process has been finished successfully."),
        Err(e) => error!("Batch process failed: {}", e),
    }
}
