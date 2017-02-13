FROM alpine:3.4

RUN apk upgrade --update && \
    apk add --update bash wget openjdk8-jre

RUN wget http://www.us.apache.org/dist/kafka/0.10.2.0/kafka_2.11-0.10.2.0.tgz

RUN tar -xvzf kafka_2.11-0.10.2.0.tgz

RUN mkdir /opt

RUN mv kafka_2.11-0.10.2.0 /opt/kafka

RUN rm kafka_2.11-0.10.2.0.tgz

ADD docker-entrypoint /docker-entrypoint

EXPOSE 9092

ENTRYPOINT ["/docker-entrypoint"]
