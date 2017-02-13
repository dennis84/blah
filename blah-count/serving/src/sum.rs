use futures::{Future, Stream};

use hyper::{self, Client, Url, Post};
use hyper::client::{HttpConnector, Request};

use serde_json::{self, Value};
use serde_merge::merge;

type Fut<T> = Box<Future<Item = T, Error = hyper::Error>>;

#[derive(Serialize, Deserialize, Debug)]
pub struct Sum {
    sum: f64,
}

#[derive(Deserialize, Debug)]
pub struct SumQuery {
    pub collection: String,
    pub prop: String,
    #[serde(rename = "filterBy")]
    pub filter_by: Option<Vec<Filter>>,
}

#[derive(Deserialize, Debug)]
pub struct Filter {
    pub prop: String,
    pub operator: String,
    pub value: String,
}

pub struct SumRepo {
    pub client: Client<HttpConnector>,
    pub elastic_url: String,
}

impl Clone for SumRepo {
    fn clone(&self) -> SumRepo {
        SumRepo {
            client: self.client.clone(),
            elastic_url: self.elastic_url.clone(),
        }
    }
}

impl SumRepo {
    pub fn sum(&self, q: SumQuery) -> Fut<Sum> {
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

fn elastic_query(q: SumQuery) -> Value {
    let script = format!("doc['{}'].value * doc['count'].value", q.prop);
    let mut query = json!({
        "query": {"bool": {"must": [
            {"term": {"collection": q.collection}}]
        }},
        "aggs": {"sum": {"sum": {"script": script}}}
    });

    if q.filter_by.is_some() {
        merge(&mut query, &filter_by(&q.filter_by.unwrap()));
    }

    query
}

fn filter_by(xs: &Vec<Filter>) -> Value {
    xs.iter().filter_map(|x| match (x.prop.as_ref(), x.operator.as_ref()) {
        ("date.from", "gte") => {
            Some(json!({"query": {"bool": {
                "filter": {"range": {"date": {"gte": x.value}}}
            }}}))
        },
        ("date.to", "lte") => {
            Some(json!({"query": {"bool": {
                "filter": {"range": {"date": {"lte": x.value}}}
            }}}))
        },
        _ => None,
    }).fold(json!({}), |mut acc, x| merge(&mut acc, &x))
}

fn parse_elastic_response(data: Value) -> Sum {
    let value = match data.pointer("/aggregations/sum/value") {
        Some(value) => value.as_f64().unwrap_or(0.0),
        None => 0.0,
    };

    Sum { sum: value }
}

#[test]
fn test_parse_elastic_response() {
    let input = json!({
        "aggregations": {
            "sum": {"value": 42.0}
        }
    });

    let output = parse_elastic_response(input);
    assert_eq!(42.0, output.sum);

    let output = parse_elastic_response(json!({}));
    assert_eq!(0.0, output.sum);
}

#[test]
fn test_elastic_query_without_filters() {
    let query = SumQuery {
        collection: "foo".to_string(),
        prop: "bar".to_string(),
        filter_by: None,
    };

    let query = elastic_query(query);
    let expected = json!({
        "query": {"bool": {"must": [{"term": {"collection": "foo"}}]}},
        "aggs": {"sum": {"sum": {
            "script": "doc['bar'].value * doc['count'].value"
        }}}
    });

    assert_eq!(expected, query);
}

#[test]
fn test_elastic_query_with_filters() {
    let query = SumQuery {
        collection: "foo".to_string(),
        prop: "bar".to_string(),
        filter_by: Some(vec![
            Filter {
                prop: "date.from".to_string(),
                operator: "gte".to_string(),
                value: "value".to_string(),
            },
        ]),
    };

    let query = elastic_query(query);
    let expected = json!({
        "query": {"bool": {
            "must": [{"term": {"collection": "foo"}}],
            "filter": {"range": {"date": {"gte": "value"}}}
        }},
        "aggs": {"sum": {"sum": {
            "script": "doc['bar'].value * doc['count'].value"
        }}}
    });

    assert_eq!(expected, query);
}
