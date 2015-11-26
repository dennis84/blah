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
# Streaming
bin/create-streaming-apps
```
