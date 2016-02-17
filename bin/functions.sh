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

create_brokers() {
  declare nb=$((${1:-3} - 1))
  echo "Create 0..$nb kafka brokers"
  curl "http://192.168.99.100:7000/api/broker/add?broker=0..$nb&cpus=1&mem=1024&options=advertised.host.name=192.168.99.100" \
    &> /dev/null

  for i in $(eval echo "{0..$nb}"); do
    echo "Start broker: $i"
    curl "http://192.168.99.100:7000/api/broker/start?broker=$i" \
      &> /dev/null
  done
}

create_job() {
  declare name="$1"
  echo "Create $name job"
  curl -H "Content-Type: application/json" \
    -d "@chronos/${name}.json" http://192.168.99.100:8081/scheduler/iso8601 \
    &> /dev/null
}

destroy_job() {
  declare name="$1"
  echo "Destroy $name job"
  curl -XDELETE -H "Content-Type: application/json" \
    "http://192.168.99.100:8081/scheduler/job/$name" \
    &> /dev/null
}

run_job() {
  declare name="$1"
  echo "Run $name job"
  curl -XPUT -H "Content-Type: application/json" \
    "http://192.168.99.100:8081/scheduler/job/$name" \
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

rand_ua() {
  declare rand=${1:-"$(($RANDOM % 10))"}
  case $rand in
    0) echo "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 \
       (KHTML, like Gecko) Ubuntu Chromium/45.0.2454.85 \
       Chrome/45.0.2454.85 Safari/537.36"
       ;; # Chromium
    1) echo "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) \
       AppleWebKit/537.36 (KHTML, like Gecko) \
       Chrome/47.0.2496.0 Safari/537.36"
       ;; # Chrome
    2) echo "Mozilla/5.0 (Windows; U; Windows NT 5.0; es-ES; rv:1.8.0.3) \
       Gecko/20060426 Firefox/1.5.0.3"
       ;; # Firefox
    3) echo "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_3) \
       AppleWebKit/537.75.14 (KHTML, like Gecko) \
       Version/7.0.3 Safari/7046A194A"
       ;; # Safari
    4) echo "Mozilla/5.0 (Windows NT 10.0) \
       AppleWebKit/537.36 (KHTML, like Gecko) \
       Chrome/42.0.2311.135 Safari/537.36 Edge/12.10136"
       ;; # Edge
    5) echo "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_3_3 like Mac OS X; en-us) \
       AppleWebKit/533.17.9 (KHTML, like Gecko) \
       Version/5.0.2 Mobile/8J2 Safari/6533.18.5"
       ;; # iPhone
    6) echo "Mozilla/5.0 (Linux; U; Android 2.2.1; en-us; Nexus One Build/FRG83) \
       AppleWebKit/533.1 (KHTML, like Gecko) \
       Version/4.0 Mobile Safari/533.1"
       ;; # Android
    7) echo "Mozilla/5.0 (iPad; U; CPU OS 4_3_3 like Mac OS X; en-us) \
       AppleWebKit/533.17.9 (KHTML, like Gecko) \
       Version/5.0.2 Mobile/8J2 Safari/6533.18.5"
       ;; # iPad
    8) echo "Mozilla/5.0 (BlackBerry; U; BlackBerry AAAA; en-US) \
       AppleWebKit/534.11+ (KHTML, like Gecko) \
       Version/X.X.X.X Mobile Safari/534.11+"
       ;; # BlackBerry
    9) echo "Mozilla/5.0 (Linux; Android 5.1.1; SM-G920F Build/LMY47X) \
       AppleWebKit/537.36 (KHTML, like Gecko) \
       Chrome/46.0.2490.76 Mobile Safari/537.36"
       ;; # Samsung S6
  esac
}

rand_item() {
  echo "item-$(($RANDOM % 50))"
}

rand_user() {
  echo "user-$(($RANDOM % 100))"
}

rand_ip() {
  echo "$(($RANDOM % 256)).$(($RANDOM % 256)).$(($RANDOM % 256)).$(($RANDOM % 256))"
}

new_pageviews() {
  declare index="$1"
  local item="$(rand_item)"
  local user="$(rand_user)"
  local ip="$(rand_ip)"
  local ua="$(rand_ua)"
  printf '{
    "item":"%s",
    "user":"%s",
    "ip": "%s",
    "userAgent":"%s"
  }' "$item" "$user" "$ip" "$ua"
}

new_purchases() {
  declare index="$1"
  local item="$(rand_item)"
  local user="$(rand_user)"
  local ip="$(rand_ip)"
  local ua="$(rand_ua)"
  printf '{
    "item":"TICKET-%s",
    "price": 20.00,
    "user":"%s",
    "ip": "%s",
    "userAgent":"%s"
  }' "$item" "$user" "$ip" "$ua"
}
