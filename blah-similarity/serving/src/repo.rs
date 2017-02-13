use std::cmp::Ordering;

use futures::{Future, Stream};

use hyper::{self, Client, Url, Post};
use hyper::client::{HttpConnector, Request};

use serde_json::{self, Value};

type Fut<T> = Box<Future<Item = T, Error = hyper::Error>>;

#[derive(Serialize, Deserialize, Debug)]
pub struct Similarity {
    item: String,
    score: f32,
}

impl PartialEq for Similarity {
    fn eq(&self, other: &Similarity) -> bool {
        self.item == other.item 
    }
}

#[derive(Deserialize, Debug)]
pub struct SimilarityQuery {
    pub items: Vec<String>,
    pub collection: Option<String>,
    pub limit: Option<u32>,
}

pub struct SimilarityRepo {
    pub client: Client<HttpConnector>,
    pub elastic_url: String,
}

impl Clone for SimilarityRepo {
    fn clone(&self) -> SimilarityRepo {
        SimilarityRepo {
            client: self.client.clone(),
            elastic_url: self.elastic_url.clone(),
        }
    }
}

impl SimilarityRepo {
    pub fn search(&self, q: SimilarityQuery) -> Fut<Vec<Similarity>> {
        let url = Url::parse(&format!("{}/similarity/similarity/_search?size=50",
                                      self.elastic_url)).unwrap();
        let mut req = Request::new(Post, url);
        let query = elastic_query(q);
        req.set_body(serde_json::to_string(&query).unwrap());

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

fn elastic_query(q: SimilarityQuery) -> Value {
     json!({
        "query": {
            "bool": {
                "must": match q.collection {
                    Some(coll) => json!([
                        {"terms": {"item": q.items}},
                        {"term": {"collection": coll}}
                    ]),
                    None => json!([
                        {"terms": {"item": q.items}}
                    ]),
                }
            }
        },
        "aggs": {
            "sims": {
                "nested": {"path": "similarities"},
                "aggs": {
                    "items": {
                        "top_hits": {
                            "sort": [{"similarities.score": {"order": "desc"}}],
                            "_source": true,
                            "size": q.limit.unwrap_or(i32::max_value() as u32)
                        }
                    }
                }
            }
        }
    })
}

fn parse_elastic_response(data: Value) -> Vec<Similarity> {
    data["aggregations"]["sims"]["items"]["hits"]["hits"].as_array().map(|items| {
        let mut items = items.iter().map(|item| {
            serde_json::from_value(item["_source"].clone()).unwrap()
        }).collect::<Vec<Similarity>>();
        items.sort_by(|a, b| match b.item.partial_cmp(&a.item).unwrap() {
            Ordering::Equal => b.score.partial_cmp(&a.score).unwrap(),
            x => x,
        });
        items.dedup();
        items
    }).unwrap_or(Vec::new())
}

#[test]
fn test_parse_elastic_response() {
    let input = json!({
        "aggregations": {
            "sims": {
                "items": {
                    "hits": {
                        "hits": [{
                            "_source": {
                                "item": "item1",
                                "score": 0.2
                            }
                        }, {
                            "_source": {
                                "item": "item2",
                                "score": 0.1
                            }
                        }, {
                            "_source": {
                                "item": "item1",
                                "score": 0.3
                            }
                        }]
                    }
                }
            }
        }
    });

    let output = parse_elastic_response(input);
    let output = serde_json::to_value(output).unwrap();
    let output = output.as_array().unwrap();

    assert_eq!(2, output.len());
    assert_eq!("item2", output[0]["item"]);
    assert_eq!("item1", output[1]["item"]);
    assert!(output[1]["score"].as_f64().unwrap() >= 0.3);
}
