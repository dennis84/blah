FROM ubuntu:14.04

COPY mesos-dns .
COPY config.json .

ADD startup /startup

CMD ["/startup"]
