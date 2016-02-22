create_brokers() {
  declare nb=$((${1:-3} - 1))
  echo "Create 0..$nb kafka brokers"
  curl -s -o /dev/null "http://$BLAH_HOST:7000/api/broker/add?broker=0..$nb&cpus=1&mem=1024&options=advertised.host.name=$BLAH_HOST"

  for i in $(eval echo "{0..$nb}"); do
    echo "Start broker: $i"
    curl -s -o /dev/null "http://$BLAH_HOST:7000/api/broker/start?broker=$i"
  done
}
