FROM blah/spark-base

ADD algo-assembly-0.1.0.jar /algo.jar

CMD spark-submit \
  --class blah.algo.Submit \
  --master spark://sparkmaster:7077 \
  /algo.jar count
