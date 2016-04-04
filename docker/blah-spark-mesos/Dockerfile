FROM java:8

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64

RUN echo "deb http://repos.mesosphere.io/ubuntu/ trusty main" \
  > /etc/apt/sources.list.d/mesosphere.list

RUN apt-key adv --keyserver keyserver.ubuntu.com --recv E56151BF
RUN apt-get -y update
RUN apt-get -y install mesos

RUN mkdir /opt/spark
RUN wget -qO- http://www.eu.apache.org/dist/spark/spark-1.6.1/spark-1.6.1.tgz \
  | tar zxvf - -C /opt/spark --strip 1

ENV SPARK_HOME /opt/spark
ENV PATH $PATH:$SPARK_HOME/bin:$SPARK_HOME/sbin

RUN $SPARK_HOME/dev/change-scala-version.sh 2.11
RUN cd $SPARK_HOME && $SPARK_HOME/build/mvn \
  -Pyarn -Phadoop-2.4 -Dscala-2.11 -DskipTests clean package

ENV MESOS_NATIVE_JAVA_LIBRARY /usr/local/lib/libmesos.so

ENV SPARK_HOME /opt/spark
