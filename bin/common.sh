is_open() {
  if ! nc "$@" &> /dev/null; then
    echo "Error: $@ is not reachable!"
    return 1
  fi
  return 0
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
