![Dashboard](/dashboard.png)

## Running

```bash
docker/build
docker-compose up
bin/create-apps

cd blah-ui/
npm install
gulp
open ./index.html
```

## Collecting

```bash
curl -POST -H 'Content-Type: application/json' "http://$BOOT2DOCKER_IP:<API-PORT>/events/view" -d '{
  "page":"home",
  "user":"user1",
  "ip": "",
  "userAgent": ""
}
```

## Training

```bash
bin/create-jobs
bin/create-streaming-apps
```

## Troubleshooting

```bash

sudo mkdir /etc/resolver
sudo chmod 755 /etc/resolver
echo "nameserver $(docker-machine ip dev)" | sudo tee -a /etc/resolver/mesos

VboxManage modifyvm "dev" --natdnshostresolver1

sudo route -n add 172.17.0.0/16 (docker-machine ip dev)

```
