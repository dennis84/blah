FROM blah/spark-base

ENV SPARK_WORKER_PORT 8888
EXPOSE 8888

ADD startup /opt/spark/sbin/worker-startup

CMD ["/opt/spark/sbin/worker-startup"]
