FROM java:8

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64

RUN echo "deb http://repos.mesosphere.io/ubuntu/ trusty main" \
  > /etc/apt/sources.list.d/mesosphere.list

RUN apt-key adv --keyserver keyserver.ubuntu.com --recv E56151BF
RUN apt-get -y update
RUN apt-get -y install mesos

RUN mkdir /opt/kafka-mesos
WORKDIR /opt/kafka-mesos

RUN wget -o /dev/null https://github.com/mesos/kafka/releases/download/0.9.4.0/kafka-mesos-0.9.4.0.jar
RUN wget https://archive.apache.org/dist/kafka/0.9.0.0/kafka_2.11-0.9.0.0.tgz
ADD entrypoint /opt/kafka-mesos/entrypoint

ENTRYPOINT ["/opt/kafka-mesos/entrypoint"]
