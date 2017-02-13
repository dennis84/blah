use futures::{Future, Stream};

use hyper::{self, Client, Url};
use hyper::client::HttpConnector;

use serde_json::{self, Value};

type Fut<T> = Box<Future<Item = T, Error = hyper::Error>>;

#[derive(Serialize, Deserialize, Debug)]
pub struct Referrer {
    url: String,
    count: u64,
}

#[derive(Deserialize, Debug)]
pub struct ReferrerQuery {
    pub limit: Option<u32>,
}

pub struct ReferrerRepo {
    pub client: Client<HttpConnector>,
    pub elastic_url: String,
}

impl Clone for ReferrerRepo {
    fn clone(&self) -> ReferrerRepo {
        ReferrerRepo {
            client: self.client.clone(),
            elastic_url: self.elastic_url.clone(),
        }
    }
}

impl ReferrerRepo {
    pub fn search(&self, q: ReferrerQuery) -> Fut<Vec<Referrer>> {
        let url = format!("{}/referrer/referrer/_search?size={}&sort=count:desc",
                          self.elastic_url,
                          q.limit.unwrap_or(100));
        let url = Url::parse(&url).unwrap();
        Box::new(self.client.get(url).and_then(|resp| {
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

fn parse_elastic_response(data: Value) -> Vec<Referrer> {
    data["hits"]["hits"].as_array().map(|items| {
        items.iter().map(|hit| {
            serde_json::from_value(hit["_source"].clone()).unwrap()
        }).collect()
    }).unwrap_or(Vec::new())
}
