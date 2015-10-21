#!/bin/sh

curl -XDELETE 'http://192.168.99.100:9200/blah'

curl -PUT 'http://192.168.99.100:9200/blah' -d '
{
  "mappings": {
    "count": {
      "properties": {
        "page": {"type": "string"},
        "date": {"type": "date", "format": "dateOptionalTime"},
        "browserFamily": {"type": "string", "index": "not_analyzed"},
        "browserMajor": {"type": "string", "index": "not_analyzed"},
        "browserMinor": {"type": "string", "index": "not_analyzed"},
        "browserPatch": {"type": "string", "index": "not_analyzed"},
        "osFamily": {"type": "string", "index": "not_analyzed"},
        "osMajor": {"type": "string", "index": "not_analyzed"},
        "osMinor": {"type": "string", "index": "not_analyzed"},
        "osPatch": {"type": "string", "index": "not_analyzed"},
        "deviceFamily": {"type": "string", "index": "not_analyzed"},
        "count": {"type": "integer"}
      }
    },
    "sims": {
      "properties": {
        "user": {"type": "string"},
        "views": {
          "type": "nested",
          "properties": {
            "page": {"type": "string"},
            "score": {"type": "double"}
          }
        }
      }
    },
    "users": {
      "properties": {
        "user": {"type": "string", "index": "not_analyzed"},
        "ip": {"type": "string", "index": "not_analyzed"},
        "lng": {"type": "double", "index": "not_analyzed"},
        "lat": {"type": "double", "index": "not_analyzed"},
        "country": {"type": "string", "index": "not_analyzed"},
        "countryCode": {"type": "string", "index": "not_analyzed"},
        "city": {"type": "string", "index": "not_analyzed"},
        "zipCode": {"type": "string", "index": "not_analyzed"}
      }
    }
  }
}'

# curl -XDELETE 'http://192.168.99.100:9200/blah/_mapping/sims'
# curl -XPUT 'http://192.168.99.100:9200/blah/_mapping/sims' -d '
# {
#   "properties" : {
#     "user" : {"type": "string"},
#     "views" : {
#       "type": "nested",
#       "properties": {
#         "page" : {"type": "string"},
#         "score" : {"type": "double"}
#       }
#     }
#   }
# }'

# curl -XPOST 'localhost:9200/blah/count/_search?pretty' -d '
# {
#   "size": 0,
#   "query": {"bool": {"must": []}},
#   "aggs": {
#     "pageviews": {
#       "date_histogram": {
#         "field": "date",
#         "interval": "day"
#       },
#       "aggs": {
#         "browserFamily": {
#           "terms": {"field": "browserFamily"},
#           "aggs": {
#             "osFamily": {
#               "terms": {"field": "osFamily"},
#               "aggs": {
#                 "osMajor": {
#                   "terms": {"field": "osMajor"}
#                 }
#               }
#             }
#           }
#         }
#       }
#     }
#   }
# }'

# curl -XGET 'http://localhost:9200/blah/sims/user1?pretty'

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
#     "filtered": {
#       "query": {
#         "bool": {
#           "must": [
#             {"match": {"page": "page1"}},
#             {"match": {"deviceFamily": "Other"}}
#           ]
#         }
#       },
#       "filter": {
#         "range": {"date": {"gte": "2015-09-13"}}
#       }
#     }
#   }
# }'

# curl -XGET 'http://localhost:9200/blah/count/_count?pretty' -d ''

# curl -XPOST 'localhost:9200/blah/users/_search?pretty' -d '
# {
#   "size": 0,
#   "query": {"bool": {"must": []}},
#   "aggs": {
#     "users": {
#       "terms": {"field": "country"}
#     }
#   }
# }'
