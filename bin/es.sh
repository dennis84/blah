#!/bin/sh

# curl -XDELETE http://localhost:9200/blah

# curl -XDELETE 'http://localhost:9200/blah/_mapping/count'
# curl -XPUT 'http://localhost:9200/blah/_mapping/count' -d '
# {
#   "properties" : {
#     "page" : {"type": "string"},
#     "date" : {"type": "date"},
#     "browserFamily": {"type": "string", "index": "not_analyzed"},
#     "browserMajor": {"type": "string"},
#     "browserMinor": {"type": "string"},
#     "browserPatch": {"type": "string"},
#     "osFamily": {"type": "string", "index": "not_analyzed"},
#     "osMajor": {"type": "string"},
#     "osMinor": {"type": "string"},
#     "osPatch": {"type": "string"},
#     "deviceFamily": {"type": "string", "index": "not_analyzed"},
#     "count": {"type": "integer"}
#   }
# }'

curl -XPOST 'localhost:9200/blah/count/_search?pretty' -d '
{
  "size": 0,
  "query": {"bool": {"must": []}},
  "aggs": {
    "pageviews": {
      "date_histogram": {
        "field": "date",
        "interval": "day",
        "format": "yyyy-MM-dd"
      },
      "aggs": {
        "browserFamily": {
          "terms": {"field": "browserFamily"}
        },
        "osFamily": {
          "terms": {"field": "osFamily"}
        }
      }
    }
  }
}'

# curl -XGET 'http://localhost:9200/blah/count/_search?pretty' -d '{
#   "query": {
#     "bool": {
#       "must": [
#         {"match": {"page": "page1"}},
#         {"match": {"browserFamily": "Chrome"}}
#       ]
#     }
#   }
# }'

# curl -XGET 'http://localhost:9200/blah/count/_count?pretty' -d '{
#   "query": {
#     "bool": {
#       "must": [
#         {"match": {"page": "page1"}},
#         {"match": {"deviceFamily": "Other"}}
#       ]
#     }
#   }
# }'
