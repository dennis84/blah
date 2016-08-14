FROM java:8

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64

RUN echo "deb http://repos.mesosphere.io/ubuntu/ trusty main" \
  > /etc/apt/sources.list.d/mesosphere.list

RUN apt-key adv --keyserver keyserver.ubuntu.com --recv E56151BF
RUN apt-get -y update
RUN apt-get -y install mesos

RUN mkdir /opt/kafka-mesos
WORKDIR /opt/kafka-mesos

RUN wget -o /dev/null https://github.com/mesos/kafka/releases/download/v0.9.5.1/kafka-mesos-0.9.5.1.jar

RUN wget https://archive.apache.org/dist/kafka/0.10.0.1/kafka_2.11-0.10.0.1.tgz

ADD docker-entrypoint /docker-entrypoint

ENTRYPOINT ["/docker-entrypoint"]
