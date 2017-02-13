use serde_json::Value;

use serde_merge::merge;

pub fn merge_aggregation(a: &mut Value, b: &Value) -> Value {
    a.pointer_mut("/aggs")
        .and_then(|x| x.as_object_mut())
        .and_then(|x| x.iter_mut().next())
        .map(|(_, mut value)| merge(&mut value, &b));
    a.clone()
}

pub fn parse_aggregation(object: &Value) -> Vec<Value> {
    let pair = object.as_object().and_then(|x| {
        for (key, value) in x.iter() {
            if let Some(buckets) = value.pointer("/buckets") {
                return Some((key, buckets))
            }
        }

        None
    });

    match pair {
        Some((key, &Value::Array(ref buckets))) if !buckets.is_empty() => {
            buckets.iter().flat_map(|bucket| {
                let key_value = bucket.pointer("/key_as_string")
                    .or(bucket.pointer("/key"))
                    .expect("No key or key_as_string");
                let count = bucket.pointer("/count/value")
                    .or(bucket.pointer("/doc_count"))
                    .expect("No count/value or doc_count");

                let mut item = json!({
                    key.clone(): key_value,
                    "count": count
                });

                let inner = parse_aggregation(&bucket);

                if inner.is_empty() {
                    return vec![item];
                }

                inner.iter().map(|inner_item| {
                    merge(&mut item, &inner_item)
                }).collect()
            }).collect()
        },
        _ => Vec::new(),
    }
}

#[test]
fn test_merge_aggregation() {
    let mut aggs = vec![
        json!({"aggs": {"country": {"terms": "..."}}}),
        json!({"aggs": {"city": {"terms": "..."}}}),
        json!({"aggs": {"street": {"terms": "..."}}}),
    ];

    let expected = json!({
        "aggs": {
            "country": {
                "terms": "...",
                "aggs": {
                    "city": {
                        "terms": "...",
                        "aggs": {
                            "street": {"terms": "..."}
                        }
                    }
                }
            }
        }
    });

    let last = aggs.pop().unwrap();
    let result = aggs.iter().rev().fold(last, |acc, x| {
        merge_aggregation(&mut x.clone(), &acc)
    });

    assert_eq!(result, expected);
}

#[test]
fn test_parse_empty_aggregation() {
    let input = json!({
        "country": {"buckets": []}
    });

    let output = parse_aggregation(&input);
    assert!(output.is_empty());
}

#[test]
fn test_parse_flat_aggregation() {
    let input = json!({
        "country": {
            "buckets": [
                {"key": "Germany", "doc_count": 3},
                {"key": "United States", "doc_count": 2}
            ]
        }
    });

    let output = parse_aggregation(&input);
    let expected = vec![
        json!({"count": 3, "country": "Germany"}),
        json!({"count": 2, "country": "United States"}),
    ];

    assert_eq!(output, expected);
}

#[test]
fn test_parse_nested_aggregation() {
    let input = json!({
        "date": {
            "buckets": [{
                "key_as_string": "2015-09-04",
                "doc_count": 3,
                "browserFamily": {
                    "buckets": [
                        {"key": "Chrome", "doc_count": 4, "count": {"value": 5}},
                        {"key": "Firefox", "doc_count": 6, "count": {"value": 7}}
                    ]
                }
            }, {
                "key_as_string": "2015-09-05",
                "doc_count": 3,
                "browserFamily": {
                    "buckets": [
                        {"key": "Chrome", "doc_count": 4, "count": {"value": 10}},
                        {"key": "Firefox", "doc_count": 6, "count": {"value": 14}}
                    ]
                }
            }]
        }
    });

    let output = parse_aggregation(&input);
    let expected = vec![
        json!({"count": 5, "date": "2015-09-04", "browserFamily": "Chrome"}),
        json!({"count": 7, "date": "2015-09-04", "browserFamily": "Firefox"}),
        json!({"count": 10, "date": "2015-09-05", "browserFamily": "Chrome"}),
        json!({"count": 14, "date": "2015-09-05", "browserFamily": "Firefox"}),
    ];

    assert_eq!(output, expected);
}
