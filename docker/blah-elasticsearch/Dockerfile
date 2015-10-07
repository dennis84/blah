FROM java:7

RUN mkdir /opt/elasticsearch
RUN wget -qO- https://download.elastic.co/elasticsearch/elasticsearch/elasticsearch-1.7.2.tar.gz \
  | tar zxvf - -C /opt/elasticsearch --strip 1

VOLUME ["/data"]

ADD elasticsearch.yml /opt/elasticsearch/config/elasticsearch.yml

WORKDIR /data

CMD ["/opt/elasticsearch/bin/elasticsearch"]

EXPOSE 9200
EXPOSE 9300
