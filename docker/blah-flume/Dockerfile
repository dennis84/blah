FROM java:8

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64

RUN mkdir /opt/hadoop
RUN wget -qO- http://www.eu.apache.org/dist/hadoop/common/hadoop-2.7.0/hadoop-2.7.0.tar.gz \
  | tar zxvf - -C /opt/hadoop --strip 1

ENV HADOOP_HOME /opt/hadoop
ENV HADOOP_HDFS_HOME /opt/hadoop

RUN mkdir /opt/flume
RUN wget -qO- http://archive.apache.org/dist/flume/1.6.0/apache-flume-1.6.0-bin.tar.gz \
  | tar zxvf - -C /opt/flume --strip 1

ADD start-flume /opt/flume/bin/start-flume
ADD flume.conf /opt/flume/conf/flume.conf
RUN sed -i '/zookeeperConnect/s/localhost/zookeeper.marathon.mesos/' /opt/flume/conf/flume.conf
RUN sed -i '/hdfs.path/s/localhost/namenode.marathon.mesos/' /opt/flume/conf/flume.conf

ENV PATH /opt/flume/bin:$PATH

CMD [ "start-flume" ]
