create_app() {
  declare name="$1"
  echo "Create $name application"
  cat "marathon/${name}.json" | sed "s/\$BLAH_HOST/$BLAH_HOST/" | \
    curl -s -o /dev/null -H "Content-Type: application/json" \
    -d @- http://$BLAH_HOST:8080/v2/apps
}

destroy_app() {
  declare name="$1"
  echo "Destroy $name application"
  curl -s -o /dev/null -XDELETE "http://$BLAH_HOST:8080/v2/apps/$name"
}
