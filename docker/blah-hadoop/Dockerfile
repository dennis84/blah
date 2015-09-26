FROM java:7

RUN apt-get update && apt-get install openssh-server -y

ENV JAVA_HOME /usr/lib/jvm/java-7-openjdk-amd64

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

ADD core-site.xml $HADOOP_CONF_DIR/core-site.xml.template
ADD hdfs-site.xml $HADOOP_CONF_DIR/hdfs-site.xml
ADD bootstrap $HADOOP_PREFIX/bin/bootstrap

RUN chmod +x $HADOOP_CONF_DIR/*-env.sh
RUN echo "export JAVA_HOME=${JAVA_HOME}" >> $HADOOP_CONF_DIR/hadoop-env.sh

RUN ssh-keygen -q -N "" -t rsa -f /root/.ssh/id_rsa
RUN cp /root/.ssh/id_rsa.pub /root/.ssh/authorized_keys

ADD ssh_config /root/.ssh/config
RUN chmod 600 /root/.ssh/config
RUN chown root:root /root/.ssh/config

RUN sed  -i "/^[^#]*UsePAM/ s/.*/#&/"  /etc/ssh/sshd_config
RUN echo "UsePAM no" >> /etc/ssh/sshd_config
RUN echo "Port 2122" >> /etc/ssh/sshd_config

RUN hdfs namenode -format

EXPOSE 8020 8022 50070 50470 50090 50495 1006 1004 50010 50020 50075 50475 8485 8480

CMD ["bootstrap"]
