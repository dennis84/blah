![Dashboard](/dashboard.png)

## Running

```bash
docker/build
docker-compose -f docker/docker-compose.yml up

cd blah-ui/
npm install
gulp
open ./index.html
```

## Collecting

```bash
curl -POST -H 'Content-Type: application/json' "http://$BOOT2DOCKER_IP:8000/events/view" -d '{
  "page":"home",
  "user":"user1",
  "ip": "",
  "userAgent": ""
}
```

## Training

```bash
docker/submit count
docker/submit user
docker/submit similarity

# Streaming
docker/submit count-streaming
docker/submit user-streaming
```
