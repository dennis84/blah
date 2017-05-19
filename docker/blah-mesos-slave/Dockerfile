FROM mesosphere/mesos-slave:1.3.0-rc1

RUN apt-get update
RUN apt-get install -y apt-transport-https curl

RUN apt-get install -y supervisor wget

ADD supervisor/mesos.conf supervisor/filebeat.conf /etc/supervisor/conf.d/

RUN wget https://artifacts.elastic.co/downloads/beats/filebeat/filebeat-5.4.0-linux-x86_64.tar.gz

RUN tar -xvzf filebeat-5.4.0-linux-x86_64.tar.gz

RUN mv filebeat-5.4.0-linux-x86_64 /opt/filebeat

RUN rm filebeat-5.4.0-linux-x86_64.tar.gz

ADD filebeat.yml /filebeat.yml

ADD docker-entrypoint /docker-entrypoint

ENTRYPOINT ["/docker-entrypoint"]

EXPOSE 5051
