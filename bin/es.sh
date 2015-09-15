#!/bin/sh

# curl -XDELETE http://localhost:9200/blah

# curl -XDELETE 'http://localhost:9200/blah/_mapping/count'
# curl -XPUT 'http://localhost:9200/blah/_mapping/count' -d '
# {
#   "properties" : {
#     "page" : {"type": "string"},
#     "date" : {"type": "date", "format": "dateOptionalTime"},
#     "browserFamily": {"type": "string", "index": "not_analyzed"},
#     "browserMajor": {"type": "string", "index": "not_analyzed"},
#     "browserMinor": {"type": "string", "index": "not_analyzed"},
#     "browserPatch": {"type": "string", "index": "not_analyzed"},
#     "osFamily": {"type": "string", "index": "not_analyzed"},
#     "osMajor": {"type": "string", "index": "not_analyzed"},
#     "osMinor": {"type": "string", "index": "not_analyzed"},
#     "osPatch": {"type": "string", "index": "not_analyzed"},
#     "deviceFamily": {"type": "string", "index": "not_analyzed"},
#     "count": {"type": "integer"}
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

curl -XGET 'http://localhost:9200/blah/count/_count?pretty' -d ''
