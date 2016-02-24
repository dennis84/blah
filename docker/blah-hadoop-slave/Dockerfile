FROM blah/hadoop-base

ADD docker-entrypoint /docker-entrypoint

RUN sed -i 's/0.0.0.0/namenode.marathon.mesos/' $HADOOP_CONF_DIR/core-site.xml

EXPOSE 50010 50020 50075 50475

ENTRYPOINT ["/docker-entrypoint"]
