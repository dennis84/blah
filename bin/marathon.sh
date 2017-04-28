create_app() {
  declare path="$1"
  echo "Create $path application"
  cat "$path" | sed "s/\$BLAH_HOST/$BLAH_HOST/" | \
    curl -s -o /dev/null -H "Content-Type: application/json" \
    -d @- http://$BLAH_HOST:8080/v2/apps
}

create_forwarding_app() {
  local port="$1"; shift
  local paths="$@"
  local script_path="$(dirname "$0")"

  echo "Add haproxy entry for $BLAH_HOST:$port with path mapping: $paths"
  cat "$script_path/forwarding.json" | \
    sed "s/\$BLAH_HOST/$BLAH_HOST/" | \
    sed "s/\$PORT/$port/" | \
    sed "s#\$PATHS#$paths#" | \
    curl -s -o /dev/null -H "Content-Type: application/json" \
    -d @- http://$BLAH_HOST:8080/v2/apps
}

destroy_app() {
  declare name="$1"
  echo "Destroy $name application"
  curl -s -o /dev/null -XDELETE "http://$BLAH_HOST:8080/v2/apps/$name"
}
