FROM ubuntu:14.04

COPY mesos-dns .
COPY config.json .

ADD startup /startup

EXPOSE 53

CMD ["/startup"]
