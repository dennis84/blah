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
bin/create-sample-data
```

## Troubleshooting

```bash
sudo mkdir /etc/resolver
sudo chmod 755 /etc/resolver
echo "nameserver $(docker-machine ip dev)" | sudo tee -a /etc/resolver/mesos

VboxManage modifyvm "dev" --natdnshostresolver1

sudo route -n add 172.17.0.0/16 (docker-machine ip dev)
```
