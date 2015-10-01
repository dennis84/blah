FROM java:7

ENV JAVA_HOME /usr/lib/jvm/java-7-openjdk-amd64

RUN apt-get update && apt-get install openssh-server -y

RUN mkdir /opt/hadoop
RUN wget -qO- http://www.eu.apache.org/dist/hadoop/common/hadoop-2.7.1/hadoop-2.7.1.tar.gz \
  | tar zxvf - -C /opt/hadoop --strip 1

ENV HADOOP_PREFIX /opt/hadoop
ENV HADOOP_HOME /opt/hadoop
ENV HADOOP_COMMON_HOME /opt/hadoop
ENV HADOOP_HDFS_HOME /opt/hadoop
ENV HADOOP_CONF_DIR /opt/hadoop/etc/hadoop
ENV PATH $PATH:$HADOOP_PREFIX/bin
ENV PATH $PATH:$HADOOP_PREFIX/sbin

VOLUME /data

ADD core-site.xml $HADOOP_CONF_DIR/core-site.xml
ADD hdfs-site.xml $HADOOP_CONF_DIR/hdfs-site.xml

RUN mkdir /data && hdfs namenode -format

EXPOSE 9000 50070 50010 50020 50075 50090
