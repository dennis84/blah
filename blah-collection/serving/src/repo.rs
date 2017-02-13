use futures::{Future, Stream};

use hyper::{self, Client, Uri, Post};
use hyper::client::{HttpConnector, Request};

use serde_json::{self, Value};

type Fut<T> = Box<Future<Item = T, Error = hyper::Error>>;

#[derive(Serialize, Deserialize, Debug)]
pub struct Collection {
    name: String,
    date: String,
    count: u64,
}

#[derive(Deserialize, Debug)]
pub struct CollectionQuery {
    pub name: String,
}

pub struct CollectionRepo {
    pub client: Client<HttpConnector>,
    pub elastic_url: String,
}

impl Clone for CollectionRepo {
    fn clone(&self) -> CollectionRepo {
        CollectionRepo {
            client: self.client.clone(),
            elastic_url: self.elastic_url.clone(),
        }
    }
}

impl CollectionRepo {
    pub fn search(&self, q: CollectionQuery) -> Fut<Vec<Collection>> {
        let url = format!("{}/collection/count/_search?size=100",
                          self.elastic_url).parse::<Uri>().unwrap();
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

fn elastic_query(q: CollectionQuery) -> Value {
    json!({
        "query": {
            "bool": {
                "must": [
                    {"term": {"name": q.name}}
                ]
            }
        }
    })
}

fn parse_elastic_response(data: Value) -> Vec<Collection> {
    data["hits"]["hits"].as_array().map(|items| {
        items.iter().map(|hit| {
            serde_json::from_value(hit["_source"].clone()).unwrap()
        }).collect()
    }).unwrap_or(Vec::new())
}

#[test]
fn test_parse_elastic_response() {
    let input = json!({
        "hits": {
            "hits": [{
                "_source": {
                    "name": "foo",
                    "date": "2017-02-27",
                    "count": 3
                }
            }]
        }
    });

    let output = parse_elastic_response(input);
    let output = serde_json::to_value(output).unwrap();
    let expected = json!([
        {"name": "foo", "date": "2017-02-27", "count": 3},
    ]);

    assert_eq!(output, expected);
}

#[test]
fn test_parse_empty_elastic_response() {
    let input = json!({"hits": {"hits": []}});
    let output = parse_elastic_response(input);
    assert!(output.is_empty());
}

#[test]
fn test_parse_error_elastic_response() {
    let input = json!({});
    let output = parse_elastic_response(input);
    assert!(output.is_empty());
}
