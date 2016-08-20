![Dashboard](/dashboard.png)

[![Build Status](https://travis-ci.org/dennis84/blah.svg?branch=master)](https://travis-ci.org/dennis84/blah)

## Setting up a Docker environment

### Linux

- Get the IP address of the `docker0` bridge (172.17.42.1)
- Edit `/lib/systemd/system/docker.service` and change the `ExecStart` command to `/usr/bin/docker daemon --bip=172.17.42.1/24 -H fd://`
- Add `172.17.42.1 api.blah.local serving.blah.local` to your `/etc/hosts`

### OS X

```bash
brew install docker docker-machine docker-compose

docker-machine create -d virtualbox --virtualbox-host-dns-resolver --virtualbox-memory 4096 --virtualbox-disk-size 20000 mesos

eval $(docker-machine env mesos)

echo "$(docker-machine ip mesos) api.blah.local serving.blah.local" | sudo tee -a /etc/hosts
```

## Running

```bash
eval $(bin/console env)
bin/console build-all
docker-compose up
bin/console create-all
open http://ui.blah.local
```

## Collecting

```bash
# Creates 100 events
bin/console samples 100
```
