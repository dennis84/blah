FROM java:8

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64

RUN echo "deb http://repos.mesosphere.io/ubuntu/ trusty main" \
  > /etc/apt/sources.list.d/mesosphere.list

RUN apt-key adv --keyserver keyserver.ubuntu.com --recv E56151BF
RUN apt-get -y update
RUN apt-get -y install mesos

RUN mkdir /opt/spark
RUN wget -qO- http://d3kbcqa49mib13.cloudfront.net/spark-2.1.0-bin-hadoop2.7.tgz \
  | tar zxvf - -C /opt/spark --strip 1

ENV SPARK_HOME /opt/spark
ENV PATH $PATH:$SPARK_HOME/bin:$SPARK_HOME/sbin

ENV MESOS_NATIVE_JAVA_LIBRARY /usr/local/lib/libmesos.so

ADD postgresql-42.0.0.jar /opt/postgresql-42.0.0.jar

ENV SPARK_HOME /opt/spark
