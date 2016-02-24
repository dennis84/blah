FROM alpine:3.3

RUN apk upgrade --update && \
    apk add --update bash

COPY mesos-dns .
COPY config.json .

ADD docker-entrypoint /docker-entrypoint

EXPOSE 53

ENTRYPOINT ["/docker-entrypoint"]
