create_app() {
  declare name="$1"
  echo "Create $name application"
  curl -H "Content-Type: application/json" \
    -d "@marathon/${name}.json" http://192.168.99.100:8080/v2/apps \
    &> /dev/null
}

destroy_app() {
  declare name="$1"
  echo "Destroy $name application"
  curl -XDELETE "http://192.168.99.100:8080/v2/apps/$name" \
    &> /dev/null
}

create_job() {
  declare name="$1"
  echo "Create $name job"
  curl -H "Content-Type: application/json" \
    -d "@chronos/${name}.json" http://192.168.99.100:8081/scheduler/iso8601 \
    &> /dev/null
}

ping_wait() {
  declare hn="$1"
  echo "Waiting for $hn"
  while ! ping -c1 "$hn" &>/dev/null; do
    echo -n "."
    sleep 1
  done
  echo ""
}
