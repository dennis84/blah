use futures::{Future, Stream};

use hyper::{self, Client, Url, Post};
use hyper::client::{HttpConnector, Request};

use serde_json::{self, Value};

use serde_merge::merge;

use util::{merge_aggregation, parse_aggregation};

type Fut<T> = Box<Future<Item = T, Error = hyper::Error>>;

#[derive(Serialize, Deserialize, Debug)]
pub struct User {
    pub user: String,
    pub date: String,
    pub email: Option<String>,
    pub firstname: Option<String>,
    pub lastname: Option<String>,
    pub lng: Option<f64>,
    pub lat: Option<f64>,
    pub country: Option<String>,
    #[serde(rename = "countryCode")]
    pub country_code: Option<String>,
    pub city: Option<String>,
    #[serde(rename = "zipCode")]
    pub zip_code: Option<String>,
    pub events: Vec<UserEvent>,
    #[serde(rename = "nbEvents")]
    pub nb_events: u64,
}

#[derive(Serialize, Deserialize, Debug)]
pub struct UserEvent {
    date: String,
    collection: String,
    item: Option<String>,
    title: Option<String>,
    ip: Option<String>,
}

#[derive(Serialize, Deserialize, Debug)]
pub struct UserCount {
    count: u64,
    #[serde(skip_serializing_if = "Option::is_none")]
    country: Option<String>,
}

#[derive(Deserialize, Debug)]
pub struct UserQuery {
    #[serde(rename = "filterBy")]
    pub filter_by: Option<Vec<Filter>>,
    #[serde(rename = "groupBy")]
    pub group_by: Option<Vec<String>>
}

#[derive(Deserialize, Debug)]
pub struct Filter {
    pub prop: String,
    pub operator: String,
    pub value: String,
}

pub struct UserRepo {
    pub client: Client<HttpConnector>,
    pub elastic_url: String,
}

impl Clone for UserRepo {
    fn clone(&self) -> UserRepo {
        UserRepo {
            client: self.client.clone(),
            elastic_url: self.elastic_url.clone(),
        }
    }
}

impl UserRepo {
    pub fn search(&self, q: UserQuery) -> Fut<Vec<User>> {
        let url = format!("{}/user/user/_search?size=50&sort=date:desc",
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
            let data = serde_json::from_slice::<Value>(&bytes).unwrap();
            data["hits"]["hits"].as_array().map(|items| {
                items.iter().map(|item| {
                    serde_json::from_value(item["_source"].clone()).unwrap()
                }).collect()
            }).unwrap_or(Vec::new())
        }))
    }

    pub fn count(&self, q: UserQuery) -> Fut<UserCount> {
        let url = format!("{}/user/user/_count",
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
            serde_json::from_slice(&bytes).unwrap_or(UserCount {
                count: 0,
                country: None,
            })
        }))
    }

    pub fn grouped(&self, q: UserQuery) -> Fut<Vec<UserCount>> {
        let url = format!("{}/user/user/_search?size=0",
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
            let value = serde_json::from_slice::<Value>(&bytes).unwrap();
            let ref aggs = value["aggregations"];
            parse_aggregation(&aggs).iter()
                .map(|x| serde_json::from_value(x.clone()).unwrap())
                .collect()
        }))
    }
}

fn elastic_query(q: UserQuery) -> Value {
    let query = match q {
        UserQuery {
            filter_by: Some(ref filters),
            group_by: None
        } => filter_by(filters),
        UserQuery {
            filter_by: Some(ref filters),
            group_by: Some(ref groups)
        } if groups.is_empty() => filter_by(filters),
        UserQuery {
            filter_by: None,
            group_by: Some(ref groups)
        } => group_by(groups),
        UserQuery {
            filter_by: Some(ref filters),
            group_by: Some(ref groups)
        } if filters.is_empty() => group_by(groups),
        UserQuery {
            filter_by: Some(ref filters),
            group_by: Some(ref groups)
        } => merge(&mut filter_by(filters), &group_by(groups)),
        _ => json!({}),
    };

    if query.as_object().unwrap().is_empty() {
        return json!({"query": {"match_all": {}}});
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
        ("user", "eq") => {
            Some(json!({"query": {"bool": {
                "must": [{"term": {"user": x.value}}]
            }}}))
        }
        ("user", "contains") => {
            Some(json!({"query": {"bool": {
                "must": [{"prefix": {"user": x.value}}]
            }}}))
        },
        _ => None,
    }).fold(json!({}), |mut acc, x| merge(&mut acc, &x))
}

fn group_by(xs: &Vec<String>) -> Value {
    xs.iter().filter_map(|group| match group.as_ref() {
        "country" => {
            Some(json!({"aggs": {"country": {"terms": {
                "field": "country",
                "size": 100
            }}}}))
        },
        _ => None,
    }).rev().fold(json!({}), |acc, x| match acc {
        Value::Object(ref map) if map.is_empty() => x.clone(),
        _ => merge_aggregation(&mut x.clone(), &acc),
    })
}

#[test]
fn test_elastic_query_none() {
    let query = UserQuery {
        filter_by: None,
        group_by: None,
    };

    let query = elastic_query(query);
    assert_eq!(json!({}), query);
}

#[test]
fn test_elastic_query_empty() {
    let query = UserQuery {
        filter_by: Some(vec![]),
        group_by: None,
    };

    let query = elastic_query(query);
    assert_eq!(json!({}), query);

    let query = UserQuery {
        filter_by: None,
        group_by: Some(vec![]),
    };

    let query = elastic_query(query);
    assert_eq!(json!({}), query);

    let query = UserQuery {
        filter_by: Some(vec![]),
        group_by: Some(vec![]),
    };

    let query = elastic_query(query);
    assert_eq!(json!({}), query);
}

#[test]
fn test_elastic_query_with_filters() {
    let query = UserQuery {
        filter_by: Some(vec![
            Filter {
                prop: "date.from".to_string(),
                operator: "gte".to_string(),
                value: "value".to_string(),
            },
            Filter {
                prop: "user".to_string(),
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
                "must": [{"term": {"user": "value"}}]
            }
        }
    });

    assert_eq!(query, expected);
}

#[test]
fn test_elastic_query_with_filters_and_empty_groups() {
    let query = UserQuery {
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
                "filter": {"range": {"date": {"gte":"value"}}}
            }
        }
    });

    assert_eq!(query, expected);
}

#[test]
fn test_elastic_query_with_groups() {
    let query = UserQuery {
        filter_by: None,
        group_by: Some(vec!["country".to_string()]),
    };

    let query = elastic_query(query);
    let expected = json!({
        "aggs": {
            "country": {
                "terms": {"field": "country", "size": 100}
            }
        }
    });

    assert_eq!(query, expected);
}

#[test]
fn test_elastic_query_with_groups_and_empty_filters() {
    let query = UserQuery {
        filter_by: Some(vec![]),
        group_by: Some(vec!["country".to_string()]),
    };

    let query = elastic_query(query);
    let expected = json!({
        "aggs": {
            "country": {
                "terms": {"field": "country", "size": 100}
            }
        }
    });

    assert_eq!(query, expected);
}

#[test]
fn test_elastic_query_with_groups_and_filters() {
    let query = UserQuery {
        filter_by: Some(vec![
            Filter {
                prop: "date.from".to_string(),
                operator: "gte".to_string(),
                value: "value".to_string(),
            },
        ]),
        group_by: Some(vec!["country".to_string()]),
    };

    let query = elastic_query(query);
    let expected = json!({
        "query": {
            "bool": {
                "filter": {"range": {"date": {"gte":"value"}}}
            }
        },
        "aggs": {
            "country": {
                "terms": {"field": "country", "size": 100}
            }
        }
    });

    assert_eq!(query, expected);
}
