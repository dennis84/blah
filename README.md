![Dashboard](/dashboard.png)

[![Build Status](https://travis-ci.org/dennis84/blah.svg?branch=master)](https://travis-ci.org/dennis84/blah)

## Setting up a Docker environment

- Get the IP address of the `docker0` bridge (172.17.42.1)
- Add `172.17.42.1 api.blah.local serving.blah.local` to your `/etc/hosts`

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
