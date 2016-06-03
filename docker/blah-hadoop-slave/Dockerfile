FROM blah/hadoop-base

ARG NAMENODE=0.0.0.0

ADD docker-entrypoint /docker-entrypoint

RUN sed -i "s/0.0.0.0/$NAMENODE/" $HADOOP_CONF_DIR/core-site.xml

EXPOSE 50010 50020 50075 50475

ENTRYPOINT ["/docker-entrypoint"]
