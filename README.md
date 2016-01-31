![Dashboard](/dashboard.png)

[![Build Status](https://travis-ci.org/dennis84/blah.svg?branch=master)](https://travis-ci.org/dennis84/blah)
[![Sauce Test Status](https://saucelabs.com/buildstatus/dennis84)](https://saucelabs.com/u/dennis84)

## Running

```bash
bin/console build-all
docker-compose up
bin/console create-all
open ./index.html
```

## Collecting

```bash
bin/console samples
```

## Troubleshooting

```bash
sudo mkdir /etc/resolver
sudo chmod 755 /etc/resolver
echo "nameserver $(docker-machine ip dev)" | sudo tee -a /etc/resolver/mesos

VboxManage modifyvm "dev" --natdnshostresolver1 on

sudo route -n add 172.17.0.0/16 (docker-machine ip dev)
```
