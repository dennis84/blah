FROM alpine:3.3

COPY mesos-dns .
COPY config.json .

ADD startup /startup

EXPOSE 53

CMD ["/startup"]
