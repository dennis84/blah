use std::cmp::Ordering;

use futures::{Future, Stream};

use hyper::{self, Client, Url, Post};
use hyper::client::{HttpConnector, Request};

use serde_json::{self, Value};

type Fut<T> = Box<Future<Item = T, Error = hyper::Error>>;

#[derive(Serialize, Deserialize, Debug)]
pub struct Recommendation {
    item: String,
    score: f32,
}

#[derive(Deserialize, Debug)]
pub struct RecommendationQuery {
    pub user: String,
    pub collection: Option<String>,
    pub limit: Option<u32>,
}

pub struct RecommendationRepo {
    pub client: Client<HttpConnector>,
    pub elastic_url: String,
}

impl Clone for RecommendationRepo {
    fn clone(&self) -> RecommendationRepo {
        RecommendationRepo {
            client: self.client.clone(),
            elastic_url: self.elastic_url.clone(),
        }
    }
}

impl RecommendationRepo {
    pub fn search(&self, q: RecommendationQuery) -> Fut<Vec<Recommendation>> {
        let url = Url::parse(&format!(
                "{}/recommendation/recommendation/_search?size=1",
                self.elastic_url)).unwrap();
        let mut req = Request::new(Post, url);
        req.set_body(serde_json::to_string(&elastic_query(q)).unwrap());

        Box::new(self.client.request(req).and_then(|resp| {
            resp.body().fold(Vec::new(), |mut acc, chunk| {
                acc.extend_from_slice(chunk.as_ref());
                Ok::<_, hyper::Error>(acc)
            })
        }).map(|bytes| {
            let resp = serde_json::from_slice(&bytes);
            parse_elastic_response(resp.unwrap())
        }))
    }
}

fn elastic_query(q: RecommendationQuery) -> Value {
     json!({
        "query": {
            "bool": {
                "must": match q.collection {
                    Some(coll) => json!([
                        {"term": {"user": q.user}},
                        {"term": {"collection": coll}}
                    ]),
                    None => json!([
                        {"term": {"user": q.user}}
                    ]),
                }
            }
        }
    })
}

fn parse_elastic_response(data: Value) -> Vec<Recommendation> {
    data["hits"]["hits"][0]["_source"]["items"].as_array().map(|items| {
        let mut items = items.iter().map(|item| {
            serde_json::from_value(item.clone()).unwrap()
        }).collect::<Vec<Recommendation>>();
        items.sort_by(|a, b| {
            b.score.partial_cmp(&a.score).unwrap_or(Ordering::Equal)
        });
        items
    }).unwrap_or(Vec::new())
}

#[test]
fn test_parse_elastic_response() {
    let input = json!({
        "hits": {
            "hits": [{
                "_source": {
                    "items": [{
                        "item": "item1",
                        "score": 0.2
                    }, {
                        "item": "item2",
                        "score": 0.3
                    }]
                }
            }]
        }
    });

    let output = parse_elastic_response(input);
    let output = serde_json::to_value(output).unwrap();
    assert_eq!("item2", output[0]["item"]);
    assert_eq!("item1", output[1]["item"]);
}

#[test]
fn test_parse_empty_elastic_response() {
    let input = json!({"hits": {"hits": []}});
    let output = parse_elastic_response(input);
    assert!(output.is_empty());
}
