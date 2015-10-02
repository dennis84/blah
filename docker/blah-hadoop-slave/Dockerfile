FROM blah/hadoop-base

ADD startup $HADOOP_PREFIX/bin/slave-startup

EXPOSE 9000 50070 50010 50020 50075 50090

CMD ["slave-startup"]
