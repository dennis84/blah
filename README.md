![Dashboard](/dashboard.png)

[![Build Status](https://travis-ci.org/dennis84/blah.svg?branch=master)](https://travis-ci.org/dennis84/blah)

# Quickstart

```bash
eval $(bin/console env)
bin/console build-all
docker-compose up
bin/console create-all
open http://localhost
```

## Create Sample Data

```bash
# Create test fixtures
bin/console fixtures

# Create randomly generated events
bin/console samples 100
```
