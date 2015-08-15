#!/bin/sh

for i in $(seq 1000); do
  page="page-$((RANDOM % 100))"
  user="user-$((RANDOM % 100))"

  curl --silent \
    -X POST \
    -H "Content-Type: application/json" \
    -H "Accept: application/json" \
    -d "{\"name\":\"view\",\"props\":{\"event\":\"${page}\",\"user\":\"${user}\"}}" \
    http://localhost:8000/events \
    > /dev/null
done
