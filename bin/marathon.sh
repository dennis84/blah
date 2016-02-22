create_app() {
  declare name="$1"
  echo "Create $name application"
  curl -s -o /dev/null -H "Content-Type: application/json" \
    -d "@marathon/${name}.json" http://$BLAH_HOST:8080/v2/apps
}

destroy_app() {
  declare name="$1"
  echo "Destroy $name application"
  curl -s -o /dev/null -XDELETE "http://$BLAH_HOST:8080/v2/apps/$name"
}
