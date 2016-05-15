![Dashboard](/dashboard.png)

[![Build Status](https://travis-ci.org/dennis84/blah.svg?branch=master)](https://travis-ci.org/dennis84/blah)

## Setting up a Docker environment

### Linux

- Get the IP address of the `docker0` bridge (172.17.42.1)
- Edit `/lib/systemd/system/docker.service` and change the `ExecStart` command to `/usr/bin/docker daemon --bip=172.17.42.1/24 --dns=172.17.42.1 --dns=8.8.8.8 -H fd://`
- Add `nameserver 172.17.42.1` to your `/etc/resolv.conf`

### OS X

```bash
brew install docker docker-machine docker-compose

docker-machine create -d virtualbox --virtualbox-host-dns-resolver --virtualbox-memory 4096 --virtualbox-disk-size 20000 mesos

sudo mkdir /etc/resolver
sudo chmod 755 /etc/resolver
echo "nameserver $(docker-machine ip mesos)" | sudo tee -a /etc/resolver/mesos

eval $(docker-machine env mesos)

sudo route -n add 172.17.0.0/16 $(docker-machine ip mesos)
```

## Running

```bash
eval $(bin/console env)
bin/console build-all
docker-compose up
bin/console create-all
open blah-ui/index.html
```

## Collecting

```bash
# Creates 100 events
bin/console samples 100
```
