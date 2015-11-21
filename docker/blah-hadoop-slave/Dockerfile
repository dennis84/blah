FROM blah/hadoop-base

ADD startup $HADOOP_PREFIX/bin/slave-startup

RUN sed -i 's/0.0.0.0/namenode.marathon.mesos/' $HADOOP_CONF_DIR/core-site.xml

EXPOSE 9000 50070 50010 50020 50075 50090

CMD ["slave-startup"]
