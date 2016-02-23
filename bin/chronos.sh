create_job() {
  declare name="$1"
  echo "Create $name job"
  curl -s -o /dev/null -H "Content-Type: application/json" \
    -d "@chronos/${name}.json" http://$BLAH_HOST:8081/scheduler/iso8601
}

destroy_job() {
  declare name="$1"
  echo "Destroy $name job"
  curl -s -o /dev/null -XDELETE -H "Content-Type: application/json" \
    "http://$BLAH_HOST:8081/scheduler/job/$name"
}

run_job() {
  declare name="$1"
  echo "Run $name job"
  curl -s -o /dev/null -XPUT -H "Content-Type: application/json" \
    "http://$BLAH_HOST:8081/scheduler/job/$name"
}