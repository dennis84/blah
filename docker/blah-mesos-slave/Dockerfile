FROM mesosphere/mesos-slave:1.1.0-rc3

RUN apt-get update
RUN apt-get install -y apt-transport-https curl

RUN apt-get install -y supervisor wget

ADD supervisor/mesos.conf supervisor/filebeat.conf /etc/supervisor/conf.d/

RUN wget --no-check-certificate https://download.elastic.co/beats/filebeat/filebeat-1.3.1-x86_64.tar.gz

RUN tar -xvzf filebeat-1.3.1-x86_64.tar.gz

RUN mv filebeat-1.3.1-x86_64 /opt/filebeat

RUN rm filebeat-1.3.1-x86_64.tar.gz

ADD filebeat.yml /filebeat.yml

ADD docker-entrypoint /docker-entrypoint

ENTRYPOINT ["/docker-entrypoint"]
