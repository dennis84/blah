#!/bin/sh

flume="$HOME/code/apache-flume-1.6.0-bin"

$flume/bin/flume-ng agent \
  --conf $flume/conf \
  --conf-file ./bin/kafka-to-hdfs.conf \
  --name a1 \
  -Dflume.root.logger=INFO,console
