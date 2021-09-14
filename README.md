# Getting Started

![Alt text](arch.png?raw=true "Lambda architecture")

## ( Current version does not include iot-producer-mqtt[simulator-mqtt] and batch-layer[hadoop] but minimal technical assignment (iot data pipeline with report) is prepared. So please drop these two projects.)
### Reference Documentation
To run application:
- Install Docker & Docker-Compose
- Run : mvn clean package 
- Run : docker-compose -p lambda-arch up
#### Create keyspace and table in cassandra
- Run : docker exec cassandra cqlsh --username cassandra --password cassandra -f /schema.cql
#### Create Kafka topic "iot-topic"
- Run : docker exec kafka kafka-topics --create --topic iot-topic --partitions 1 --replication-factor 1 --if-not-exists --zookeeper zookeeper:2181
#### Configure connection between MQTT and kafka
- Run : curl -d @container-resources/connect-mqtt-source.json -H "Content-Type: application/json" -X POST http://localhost:8083/connectors
####  Create folders on Hadoop file system and total permission to those
- Run : docker exec namenode hdfs dfs -rm /store
- Run : docker exec namenode hdfs dfs -mkdir /store
- Run : docker exec namenode hdfs dfs -mkdir /store/checkpoint
- Run : docker exec namenode hdfs dfs -chmod -R 777 /store
- Run : docker exec namenode hdfs dfs -chmod -R 777 /store/checkpoint

#### To run speed-layer(spark-processing) on the spark-master in docker container:
- Run spark processor : docker exec spark-master /spark/bin/spark-submit --class com.relay42.streaming.SparkStreamApplication --master spark://localhost:7077 /opt/spark-data/spark-processor-0.0.1-SNAPSHOT.jar

#### To run batch-layer(spark-processing) on the spark-master in docker container:
- Run spark processor : docker exec spark-master /spark/bin/spark-submit --class com.relay42.batching.BatchApplication --master spark://localhost:7077 /opt/spark-data/hadoop-0.0.1-SNAPSHOT.jar


#### To run IoT device data simulator run locally iot-producer-kafka[simulator-kafka] and iot-producer-mqtt[simulator-mqtt] :
-  /iot-producer-kafka/mvn spring-boot:run
-  /iot-producer-mqtt/mvn spring-boot:run

#### To run Serving layer and get APIs run serving-layer[serving]:
-  /serving-layer/mvn spring-boot:run

#### Check mosquitto server and topic:
docker run -it --rm --name mqtt-publisher --network lambda-arch_default efrecon/mqtt-client pub -h mosquitto  -t "iot-topic" -m "{device:'device5',date:'1984-08-08', value=37}"

#### Check Kafka
- docker exec kafka usr/bin/kafka-topics --create --topic iot-topic --partitions 1 --replication-factor 1 --if-not-exists --zookeeper zookeeper:2181
- docker exec kafka usr/bin/kafka-console-producer --request-required-acks 1 --broker-list kafka:9092 --topic iot-topic
- docker exec kafka usr/bin/kafka-console-consumer --bootstrap-server kafka:9092 --topic iot-topic
- docker exec kafka usr/bin/kafka-topics --list --zookeeper zookeeper:2181

#### Check Cassandra 
- docker exec cassandra cqlsh --username cassandra --password cassandra
- List data:  SELECT * FROM ks_iot.t_message;

#### to call  API :
- curl -X POST 'http://localhost:8085/api/device/search' -H 'Content-Type: application/json' --data-raw '{ "devices": ["device1"], "to": "2021-09-14 08:08:29.700","from": "2021-09-14 08:08:25.639"}'
- curl -X POST 'http://localhost:8085/api/device/min' -H 'Content-Type: application/json' --data-raw '{ "devices": ["device1"] ,"from": "2021-09-14 08:08:25.639", "to": "2021-09-14 08:08:29.700"}'
- curl -X POST 'http://localhost:8085/api/device/max' -H 'Content-Type: application/json' --data-raw '{ "devices": ["device1"] ,"from": "2021-09-14 08:08:25.639", "to": "2021-09-14 08:08:29.700"}'
- curl -X POST 'http://localhost:8085/api/device/med' -H 'Content-Type: application/json' --data-raw '{ "devices": ["device1"] ,"from": "2021-09-14 08:08:25.639", "to": "2021-09-14 08:08:29.700"}'
- curl -X POST 'http://localhost:8085/api/device/avg' -H 'Content-Type: application/json' --data-raw '{ "devices": ["device1"] ,"from": "2021-09-14 08:08:25.639", "to": "2021-09-14 08:08:29.700"}'
