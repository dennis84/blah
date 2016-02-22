is_ok() {
  [[ "200" == $(curl -s $1 -o /dev/null -w '%{http_code}\n') ]] && return 0
  return 1
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
