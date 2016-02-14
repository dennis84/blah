FROM java:8

RUN mkdir /opt/elasticsearch
RUN wget -qO- https://download.elastic.co/elasticsearch/elasticsearch/elasticsearch-2.2.0.tar.gz \
  | tar zxvf - -C /opt/elasticsearch --strip 1

ADD elasticsearch.yml /opt/elasticsearch/config/elasticsearch.yml

RUN useradd elasticsearch
RUN chown -R elasticsearch:elasticsearch /opt/elasticsearch
USER elasticsearch

CMD ["/opt/elasticsearch/bin/elasticsearch"]

EXPOSE 9200
EXPOSE 9300
