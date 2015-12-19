![Dashboard](/dashboard.png)

## Running

```bash
bin/build
docker-compose up
bin/create-all

cd blah-ui/
npm install
gulp
open ./index.html
```

## Collecting

```bash
curl -POST -H 'Content-Type: application/json' "http://serving.marathon.mesos:8001/events/view" -d '{
  "page":"home",
  "user":"user1",
  "ip": "",
  "userAgent": ""
}
```

## Troubleshooting

```bash

sudo mkdir /etc/resolver
sudo chmod 755 /etc/resolver
echo "nameserver $(docker-machine ip dev)" | sudo tee -a /etc/resolver/mesos

VboxManage modifyvm "dev" --natdnshostresolver1

sudo route -n add 172.17.0.0/16 (docker-machine ip dev)

```
