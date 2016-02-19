FROM blah/java

RUN apk add --update openssh

RUN wget http://www.eu.apache.org/dist/hadoop/common/hadoop-2.7.1/hadoop-2.7.1.tar.gz

RUN tar -xvzf hadoop-2.7.1.tar.gz

RUN mv hadoop-2.7.1 /usr/share/hadoop

RUN rm hadoop-2.7.1.tar.gz

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

VOLUME ["/data/dfs/name", "/data/dfs/data"]
