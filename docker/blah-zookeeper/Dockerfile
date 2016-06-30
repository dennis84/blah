FROM alpine:3.3

RUN apk upgrade --update && \
    apk add --update bash wget openjdk8-jre

RUN wget http://apache.mirrors.pair.com/zookeeper/zookeeper-3.4.8/zookeeper-3.4.8.tar.gz

RUN tar -xvzf zookeeper-3.4.8.tar.gz

RUN mkdir /opt

RUN mv zookeeper-3.4.8 /opt/zookeeper

RUN rm zookeeper-3.4.8.tar.gz

RUN cp /opt/zookeeper/conf/zoo_sample.cfg /opt/zookeeper/conf/zoo.cfg

RUN mkdir -p /tmp/zookeeper

EXPOSE 2181 2888 3888

WORKDIR /opt/zookeeper

VOLUME ["/opt/zookeeper/conf", "/tmp/zookeeper"]

ENTRYPOINT ["/opt/zookeeper/bin/zkServer.sh"]

CMD ["start-foreground"]
