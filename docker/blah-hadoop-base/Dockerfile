FROM alpine:3.3

RUN apk upgrade --update && \
    apk add --update bash wget openjdk8 openrc openssh

ENV JAVA_HOME /usr/lib/jvm/java-1.8-openjdk

RUN wget http://www.eu.apache.org/dist/hadoop/common/hadoop-2.7.2/hadoop-2.7.2.tar.gz

RUN tar -xvzf hadoop-2.7.2.tar.gz

RUN mkdir opt

RUN mv hadoop-2.7.2 /opt/hadoop

RUN rm hadoop-2.7.2.tar.gz

ENV HADOOP_PREFIX /opt/hadoop
ENV HADOOP_HOME /opt/hadoop
ENV HADOOP_COMMON_HOME /opt/hadoop
ENV HADOOP_HDFS_HOME /opt/hadoop
ENV HADOOP_CONF_DIR /opt/hadoop/etc/hadoop
ENV PATH $PATH:$HADOOP_PREFIX/bin
ENV PATH $PATH:$HADOOP_PREFIX/sbin

ADD core-site.xml $HADOOP_CONF_DIR/core-site.xml
ADD hdfs-site.xml $HADOOP_CONF_DIR/hdfs-site.xml

RUN mkdir -p /data/dfs/name
RUN mkdir -p /data/dfs/data

RUN rc-update add sshd

VOLUME ["/data/dfs/name", "/data/dfs/data"]
