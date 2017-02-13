use futures::{Future, Stream};

use hyper::{self, Client, Url, Post};
use hyper::client::{HttpConnector, Request};

use serde_json::{self, Value};
use serde_merge::merge;

use util::{merge_aggregation, parse_aggregation};

type Fut<T> = Box<Future<Item = T, Error = hyper::Error>>;

#[derive(Serialize, Deserialize, Debug)]
pub struct Count {
    count: u64,
    #[serde(skip_serializing_if = "Option::is_none")]
    date: Option<String>,
    #[serde(rename = "browserFamily")]
    #[serde(skip_serializing_if = "Option::is_none")]
    browser_family: Option<String>,
    #[serde(rename = "browserMajor")]
    #[serde(skip_serializing_if = "Option::is_none")]
    browser_major: Option<String>,
    #[serde(rename = "osFamily")]
    #[serde(skip_serializing_if = "Option::is_none")]
    os_family: Option<String>,
    #[serde(rename = "deviceFamily")]
    #[serde(skip_serializing_if = "Option::is_none")]
    device_family: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    platform: Option<String>,
}

#[derive(Deserialize, Debug)]
pub struct CountQuery {
    pub collection: String,
    #[serde(rename = "filterBy")]
    pub filter_by: Option<Vec<Filter>>,
    #[serde(rename = "groupBy")]
    pub group_by: Option<Vec<String>>,
}

#[derive(Deserialize, Debug)]
pub struct Filter {
    pub prop: String,
    pub operator: String,
    pub value: String,
}

pub struct CountRepo {
    pub client: Client<HttpConnector>,
    pub elastic_url: String,
}

impl Clone for CountRepo {
    fn clone(&self) -> CountRepo {
        CountRepo {
            client: self.client.clone(),
            elastic_url: self.elastic_url.clone(),
        }
    }
}

impl CountRepo {
    pub fn count(&self, q: CountQuery) -> Fut<Count> {
        let url = format!("{}/count/count/_search?size=0",
                          self.elastic_url);
        let url = Url::parse(&url).unwrap();
        let mut req = Request::new(Post, url);
        let query = elastic_query(q);
        req.set_body(serde_json::to_string(&query).unwrap());

        Box::new(self.client.request(req).and_then(|resp| {
            resp.body().fold(Vec::new(), |mut acc, chunk| {
                acc.extend_from_slice(chunk.as_ref());
                Ok::<_, hyper::Error>(acc)
            })
        }).map(|bytes| {
            let data = serde_json::from_slice::<Value>(&bytes).unwrap();
            let count = match data.pointer("/aggregations/count/value") {
                Some(value) => value.as_f64().unwrap_or(0.0) as u64,
                None => 0,
            };

            Count {
                count: count,
                date: None,
                browser_family: None,
                browser_major: None,
                os_family: None,
                device_family: None,
                platform: None,
            }
        }))
    }

    pub fn grouped(&self, q: CountQuery) -> Fut<Vec<Count>> {
        let url = format!("{}/count/count/_search?size=0",
                          self.elastic_url);
        let url = Url::parse(&url).unwrap();
        let mut req = Request::new(Post, url);
        req.set_body(serde_json::to_string(&elastic_query(q)).unwrap());

        Box::new(self.client.request(req).and_then(|resp| {
            resp.body().fold(Vec::new(), |mut acc, chunk| {
                acc.extend_from_slice(chunk.as_ref());
                Ok::<_, hyper::Error>(acc)
            })
        }).map(|bytes| {
            let value = serde_json::from_slice::<Value>(&bytes).unwrap();
            let ref aggs = value["aggregations"];
            parse_aggregation(&aggs).iter()
                .map(|x| serde_json::from_value(x.clone()).unwrap())
                .collect()
        }))
    }
}

fn elastic_query(q: CountQuery) -> Value {
    let mut query = json!({
        "query": {"bool": {"must": [
            {"term": {"collection": q.collection}}]
        }}
    });

    let filters = q.filter_by.unwrap_or(vec![]);
    merge(&mut query, &filter_by(&filters));

    if q.group_by.is_none() {
        merge(&mut query, &json!({
            "aggs": {"count": {"sum": {"field": "count"}}}
        }));
    } else {
        let groups = q.group_by.unwrap();
        merge(&mut query, &group_by(&groups));
    }

    query
}

fn filter_by(xs: &Vec<Filter>) -> Value {
    xs.iter().filter_map(|x| match (x.prop.as_ref(), x.operator.as_ref()) {
        ("item", "eq") => {
            Some(json!({"query": {"bool": {
                "must": [{"term": {"item": x.value}}]
            }}}))
        },
        ("item", "ne") => {
            Some(json!({"query": {"bool": {
                "must_not": [{"term": {"item": x.value}}]
            }}}))
        },
        ("user_agent.device.family", "eq") => {
            Some(json!({"query": {"bool": {
                "must": [{"term": {"deviceFamily": x.value}}]
            }}}))
        },
        ("user_agent.device.family", "ne") => {
            Some(json!({"query": {"bool": {
                "must_not": [{"term": {"deviceFamily": x.value}}]
            }}}))
        },
        ("user_agent.browser.family", "eq") => {
            Some(json!({"query": {"bool": {
                "must": [{"term": {"browserFamily": x.value}}]
            }}}))
        },
        ("user_agent.browser.family", "ne") => {
            Some(json!({"query": {"bool": {
                "must_not": [{"match": {"browserFamily": x.value}}]
            }}}))
        },
        ("user_agent.browser.major", "eq") => {
            Some(json!({"query": {"bool": {
                "must": [{"term": {"browserMajor": x.value}}]
            }}}))
        },
        ("user_agent.browser.major", "ne") => {
            Some(json!({"query": {"bool": {
                "must_not": [{"term": {"browserMajor": x.value}}]
            }}}))
        },
        ("user_agent.os.family", "eq") => {
            Some(json!({"query": {"bool": {
                "must": [{"term": {"osFamily": x.value}}]
            }}}))
        },
        ("user_agent.os.family", "ne") => {
            Some(json!({"query": {"bool": {
                "must_not": [{"term": {"osFamily": x.value}}]
            }}}))
        },
        ("user_agent.os.major", "eq") => {
            Some(json!({"query": {"bool": {
                "must": [{"term": {"osMajor": x.value}}]
            }}}))
        },
        ("user_agent.os.major", "ne") => {
            Some(json!({"query": {"bool": {
                "must_not": [{"term": {"osMajor": x.value}}]
            }}}))
        },
        ("user_agent.platform", "eq") => {
            Some(json!({"query": {"bool": {
                "must": [{"term": {"platform": x.value}}]
            }}}))
        },
        ("user_agent.platform", "ne") => {
            Some(json!({"query": {"bool": {
                "must_not": [{"term": {"platform": x.value}}]
            }}}))
        },
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

fn group_by(xs: &Vec<String>) -> Value {
    let mut aggs = vec![
        json!({"aggs": {"date": {
            "date_histogram": {"field": "date", "interval": "day"},
            "aggs": {"count": {"sum": {"field": "count"}}}
        }}}),
    ];

    for group in xs.iter() {
        match group.as_ref() {
            "date.hour" => {
                aggs[0] = json!({"aggs": {"date": {
                    "date_histogram": {"field": "date", "interval": "hour"},
                    "aggs": {"count": {"sum": {"field": "count"}}}
                }}});
            },
            "date.month" => {
                aggs[0] =json!({"aggs": {"date": {
                    "date_histogram": {"field": "date", "interval": "month"},
                    "aggs": {"count": {"sum": {"field": "count"}}}
                }}});
            },
            "date.year" => {
                aggs[0] = json!({"aggs": {"date": {
                    "date_histogram": {"field": "date", "interval": "year"},
                    "aggs": {"count": {"sum": {"field": "count"}}}
                }}});
            },
            "user_agent.browser.family" => {
                aggs.push(json!({"aggs": {"browserFamily": {
                    "terms": {"field": "browserFamily", "size": 100},
                    "aggs": {"count": {"sum": {"field": "count"}}}
                }}}));
            },
            "user_agent.browser.major" => {
                aggs.push(json!({"aggs": {"browserMajor": {
                    "terms": {"field": "browserMajor", "size": 100},
                    "aggs": {"count": {"sum": {"field": "count"}}}
                }}}));
            },
            "user_agent.os.family" => {
                aggs.push(json!({"aggs": {"osFamily": {
                    "terms": {"field": "osFamily", "size": 100},
                    "aggs": {"count": {"sum": {"field": "count"}}}
                }}}));
            },
            "user_agent.device.family" => {
                aggs.push(json!({"aggs": {"deviceFamily": {
                    "terms": {"field": "deviceFamily", "size": 100},
                    "aggs": {"count": {"sum": {"field": "count"}}}
                }}}));
            },
            "user_agent.platform" => {
                aggs.push(json!({"aggs": {"platform": {
                    "terms": {"field": "platform", "size": 100},
                    "aggs": {"count": {"sum": {"field": "count"}}}
                }}}));
            },
            _ => (),
        }
    }

    aggs.iter().rev().fold(json!({}), |acc, x| match acc {
        Value::Object(ref map) if map.is_empty() => x.clone(),
        _ => merge_aggregation(&mut x.clone(), &acc),
    })
}

#[test]
fn test_elastic_query_none() {
    let query = CountQuery {
        collection: "foo".to_string(),
        filter_by: None,
        group_by: None,
    };

    let query = elastic_query(query);
    let expected = json!({
        "query":{"bool":{"must":[
            {"term":{"collection":"foo"}}
        ]}},
        "aggs": {"count": {"sum": {"field": "count"}}}
    });

    assert_eq!(expected, query);
}

#[test]
fn test_elastic_query_empty() {
    let expected = json!({
        "query":{"bool":{"must":[
            {"term":{"collection":"foo"}}
        ]}},
        "aggs": {"count": {"sum": {"field": "count"}}}
    });

    let query = CountQuery {
        collection: "foo".to_string(),
        filter_by: Some(vec![]),
        group_by: None,
    };

    let query = elastic_query(query);
    assert_eq!(expected, query);
}

#[test]
fn test_elastic_query_with_filters() {
    let query = CountQuery {
        collection: "foo".to_string(),
        filter_by: Some(vec![
            Filter {
                prop: "date.from".to_string(),
                operator: "gte".to_string(),
                value: "value".to_string(),
            },
            Filter {
                prop: "item".to_string(),
                operator: "eq".to_string(),
                value: "value".to_string(),
            },
        ]),
        group_by: None,
    };

    let query = elastic_query(query);
    let expected = json!({
        "query": {
            "bool": {
                "filter": {"range": {"date": {"gte":"value"}}},
                "must": [
                    {"term":{"collection":"foo"}},
                    {"term": {"item": "value"}}
                ]
            }
        },
        "aggs": {"count": {"sum": {"field": "count"}}}
    });

    assert_eq!(query, expected);
}

#[test]
fn test_elastic_query_with_filters_and_empty_groups() {
    let query = CountQuery {
        collection: "foo".to_string(),
        filter_by: Some(vec![
            Filter {
                prop: "date.from".to_string(),
                operator: "gte".to_string(),
                value: "value".to_string(),
            },
        ]),
        group_by: Some(vec![]),
    };

    let query = elastic_query(query);
    let expected = json!({
        "query": {
            "bool": {
                "filter": {"range": {"date": {"gte": "value"}}},
                "must": [{"term":{"collection":"foo"}}]
            }
        },
        "aggs": {"date": {
            "date_histogram": {"field": "date", "interval": "day"},
            "aggs": {"count": {"sum": {"field": "count"}}}
        }}
    });

    assert_eq!(query, expected);
}

#[test]
fn test_elastic_query_with_filters_and_groups() {
    let query = CountQuery {
        collection: "foo".to_string(),
        filter_by: Some(vec![
            Filter {
                prop: "item".to_string(),
                operator: "eq".to_string(),
                value: "value".to_string(),
            },
        ]),
        group_by: Some(vec![
            "date.hour".to_string(),
            "user_agent.browser.family".to_string(),
        ]),
    };

    let query = elastic_query(query);
    let expected = json!({
        "query": {
            "bool": {
                "must": [
                    {"term":{"collection": "foo"}},
                    {"term":{"item": "value"}}
                ]
            }
        },
        "aggs": {
            "date": {
                "date_histogram": {"field": "date", "interval": "hour"},
                "aggs": {
                    "count": {"sum": {"field": "count"}},
                    "browserFamily": {
                        "terms": {"field": "browserFamily", "size": 100},
                        "aggs": {"count": {"sum": {"field": "count"}}},
                    }
                }
            }
        }
    });

    assert_eq!(query, expected);
}
