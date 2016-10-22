FROM mesosphere/mesos-slave:1.0.1-2.0.93.ubuntu1404

ARG DOCKER_VERSION
ENV DOCKER_VERSION ${DOCKER_VERSION}-0~trusty

RUN apt-get update
RUN apt-get install -y apt-transport-https

RUN apt-key adv --keyserver hkp://p80.pool.sks-keyservers.net:80 \
  --recv-keys 58118E89F3A912897C070ADBF76221572C52609D

RUN echo "deb https://apt.dockerproject.org/repo ubuntu-trusty main" > \
  /etc/apt/sources.list.d/docker.list

RUN apt-get update
RUN apt-get install -y --force-yes docker-engine=${DOCKER_VERSION}

RUN apt-get install -y supervisor wget

ADD supervisor/mesos.conf supervisor/filebeat.conf /etc/supervisor/conf.d/

RUN wget --no-check-certificate https://download.elastic.co/beats/filebeat/filebeat-1.3.1-x86_64.tar.gz

RUN tar -xvzf filebeat-1.3.1-x86_64.tar.gz

RUN mv filebeat-1.3.1-x86_64 /opt/filebeat

RUN rm filebeat-1.3.1-x86_64.tar.gz

ADD filebeat.yml /filebeat.yml

ADD docker-entrypoint /docker-entrypoint

ENTRYPOINT ["/docker-entrypoint"]
