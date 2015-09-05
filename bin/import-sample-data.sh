#!/bin/sh

for i in $(seq 1000); do
  page="page$((RANDOM % 100))"
  user="user$((RANDOM % 100))"
  ua="Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2496.0 Safari/537.36"

  curl --silent \
    -X POST \
    -H "Content-Type: application/json" \
    -H "Accept: application/json" \
    -d "{\"name\":\"view\",\"props\":{\"page\":\"${page}\",\"user\":\"${user}\",\"userAgent\":\"${ua}\"}}" \
    http://localhost:8000/events \
    > /dev/null
done
