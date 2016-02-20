FROM alpine:3.3

RUN apk upgrade --update && \
    apk add --update bash

COPY mesos-dns .
COPY config.json .

ADD startup /startup

EXPOSE 53

CMD ["/startup"]
