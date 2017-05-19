is_open() {
  if ! nc -z "$@" &> /dev/null; then
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

print_setenv() {
  case $SHELL in
    */fish) echo "set -x $1 $2;"; ;;
    *)      echo "export $1=$2;"; ;;
  esac
}

find_docker_ip() {
  unamestr="$(uname)"
  if [[ "$unamestr" == 'Darwin' ]]; then
    ifconfig | grep -E "([0-9]{1,3}\.){3}[0-9]{1,3}" | grep -v 127.0.0.1 | awk '{ print $2 }' | cut -f2 -d: | head -n1
  else
    ip addr show docker0 | grep 'inet ' | cut -d/ -f1 | awk '{print $2}'
  fi
}
