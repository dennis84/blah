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
