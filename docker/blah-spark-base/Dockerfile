FROM blah/hadoop

RUN mkdir /opt/spark
RUN wget -qO- http://www.eu.apache.org/dist/spark/spark-1.5.0/spark-1.5.0.tgz \
  | tar zxvf - -C /opt/spark --strip 1

ENV SPARK_HOME /opt/spark
ENV PATH $PATH:$SPARK_HOME/bin
ENV PATH $PATH:$SPARK_HOME/sbin

RUN mkdir /opt/maven
RUN wget -qO- http://www.eu.apache.org/dist/maven/maven-3/3.3.3/binaries/apache-maven-3.3.3-bin.tar.gz \
  | tar zxvf - -C /opt/maven --strip 1

ENV PATH $PATH:/opt/maven/bin
ENV MAVEN_OPTS -Xmx2g -XX:MaxPermSize=512M -XX:ReservedCodeCacheSize=512m

RUN $SPARK_HOME/dev/change-scala-version.sh 2.11
RUN cd $SPARK_HOME && mvn -Pyarn -Phadoop-2.4 -Dscala-2.11 -DskipTests clean package
