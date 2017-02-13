use futures::{Future, Stream};

use hyper::{self, Client, Url, Post};
use hyper::client::{HttpConnector, Request};

use serde_json::{self, Value};

type Fut<T> = Box<Future<Item = T, Error = hyper::Error>>;

#[derive(Serialize, Deserialize, Debug)]
pub struct MostViewed {
    item: String,
    count: u32,
}

#[derive(Serialize, Deserialize, Debug)]
pub struct MostViewedQuery {
    pub collection: String,
    pub limit: Option<u32>,
}

pub struct MostViewedRepo {
    pub client: Client<HttpConnector>,
    pub elastic_url: String,
}

impl Clone for MostViewedRepo {
    fn clone(&self) -> MostViewedRepo {
        MostViewedRepo {
            client: self.client.clone(),
            elastic_url: self.elastic_url.clone(),
        }
    }
}

impl MostViewedRepo {
    pub fn search(&self, q: MostViewedQuery) -> Fut<Vec<MostViewed>> {
        let url = Url::parse(&format!("{}/count/count/_search?size=0",
                                      self.elastic_url)).unwrap();
        let mut req = Request::new(Post, url);
        req.set_body(serde_json::to_string(&elastic_query(q)).unwrap());

        Box::new(self.client.request(req).and_then(|resp| {
            resp.body().fold(Vec::new(), |mut acc, chunk| {
                acc.extend_from_slice(chunk.as_ref());
                Ok::<_, hyper::Error>(acc)
            })
        }).map(|bytes| {
            parse_elastic_response(serde_json::from_slice(&bytes).unwrap())
        }))
    }
}

fn elastic_query(q: MostViewedQuery) -> Value {
    json!({
        "query": {
            "bool": {
                "must": [
                    {"term": {"collection": q.collection}}
                ]
            }
        },
        "aggs": {
            "item": {
                "terms": {
                    "field": "item",
                    "size": q.limit.unwrap_or(i32::max_value() as u32),
                    "order": {"count": "desc"}
                },
                "aggs": {
                    "count": {
                        "sum": {"field": "count"}
                    }
                }
            }
        }
    })
}

fn parse_elastic_response(data: Value) -> Vec<MostViewed> {
    data["aggregations"]["item"]["buckets"].as_array().map(|buckets| {
        buckets.iter().map(|bucket| {
            let key = bucket["key"].clone();
            let key = serde_json::from_value(key).unwrap();
            let count = bucket["count"]["value"].clone();
            let count = serde_json::from_value(count).unwrap();
            MostViewed { item: key, count: count }
        }).collect()
    }).unwrap_or(Vec::new())
}

#[test]
fn test_parse_elastic_response() {
    let input = json!({
        "aggregations": {
            "item": {
                "buckets": [{
                    "key": "item1",
                    "doc_count": 1,
                    "count": {"value": 3}
                }, {
                    "key": "item2",
                    "doc_count": 1,
                    "count": {"value": 2}
                }, {
                    "key": "item3",
                    "doc_count": 1,
                    "count": {"value": 1}
                }]
            }
        }
    });

    let output = parse_elastic_response(input);
    let output = serde_json::to_value(output).unwrap();
    let expected = json!([
        {"item": "item1", "count": 3},
        {"item": "item2", "count": 2},
        {"item": "item3", "count": 1},
    ]);

    assert_eq!(output, expected);
}
