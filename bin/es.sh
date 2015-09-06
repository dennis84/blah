#!/bin/sh

# curl -XGET 'http://localhost:9200/blah/count/_search' -d '{
#   "query": {
#     "bool": {
#       "must": [
#         {"match": {"page": "page1"}},
#         {"match": {"browserFamily": "Chrome"}}
#       ]
#     }
#   }
# }' | python -m json.tool

curl -XGET 'http://localhost:9200/blah/count/_count' -d '{
  "query": {
    "bool": {
      "must": [
        {"match": {"page": "page1"}}
      ]
    }
  }
}' | python -m json.tool
