package com.relay42.streaming;

import com.relay42.streaming.model.Message;
import com.relay42.streaming.processor.MessageProcessor;
import com.relay42.streaming.util.LatestOffSetReader;
import com.relay42.streaming.util.MessageDeserializer;
import com.relay42.streaming.util.MessageOffsetCommitCallback;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class StreamingOrchestrator implements Serializable {

    private static final Logger logger = Logger.getLogger(StreamingOrchestrator.class);
    private final String hdfsFile;
    private final String checkpointFile;
    private final String brokerList;
    private final String kafkaTopic;
    private final String resetType;
    private final String cassandraHost;
    private final String cassandraPort;
    private final String cassandraUsername;
    private final String cassandraPass;
    private final String cassandraKeepAlive;
    private final String sparkAppName;
    private final String sparkMaster;

    public StreamingOrchestrator(@Value("${hadoop.server.hdfs}") String hdfsFile,
                                 @Value("${spark.server.checkpoint.dir}") String checkpointFile,
                                 @Value("${kafka.server.brokerList}") String brokerList,
                                 @Value("${kafka.server.topic}") String kafkaTopic,
                                 @Value("${kafka.server.resetType}") String resetType,
                                 @Value("${cassandra.server.host}") String cassandraHost,
                                 @Value("${cassandra.server.port}") String cassandraPort,
                                 @Value("${cassandra.server.username}") String cassandraUsername,
                                 @Value("${cassandra.server.password}") String cassandraPass,
                                 @Value("${cassandra.server.keepAliveMS}") String cassandraKeepAlive,
                                 @Value("${spark.server.app.name}") String sparkAppName,
                                 @Value("${spark.server.master}") String sparkMaster) {

        this.hdfsFile = hdfsFile;
        this.checkpointFile = checkpointFile;
        this.brokerList = brokerList;
        this.kafkaTopic = kafkaTopic;
        this.resetType = resetType;
        this.cassandraHost = cassandraHost;
        this.cassandraPort = cassandraPort;
        this.cassandraUsername = cassandraUsername;
        this.cassandraPass = cassandraPass;
        this.cassandraKeepAlive = cassandraKeepAlive;
        this.sparkAppName = sparkAppName;
        this.sparkMaster = sparkMaster;
    }

    public void start() throws Exception {
        logger.info("Starting Stream Processing");
        Map<String, Object> kafkaProperties = getKafkaProperties();
        SparkConf sparkProperties = getSparkProperties().setJars(JavaSparkContext.jarOfClass(this.getClass()));
        JavaStreamingContext streamingContext = new JavaStreamingContext(sparkProperties, Durations.seconds(5));
        streamingContext.checkpoint(checkpointFile);
        SparkSession sparkSession = SparkSession.builder().config(sparkProperties).getOrCreate();
        Map<TopicPartition, Long> offsets = getOffsets(hdfsFile, sparkSession);
        JavaInputDStream<ConsumerRecord<String, Message>> stream = createStream(streamingContext, kafkaProperties, offsets);
        MessageProcessor messageProcessor = new MessageProcessor(stream);
        messageProcessor.map()
                .persistToBatchLayer(sparkSession, hdfsFile)
                .cache()
                .persistInServingLayer();
//                .calculateAggregations();
        commitOffset(stream);
        streamingContext.start();
        streamingContext.awaitTermination();
    }

    private Map<TopicPartition, Long> getOffsets(final String hdfsFile, final SparkSession sparkSession) {
        try {
            LatestOffSetReader latestOffSetReader = new LatestOffSetReader(sparkSession, hdfsFile);
            return latestOffSetReader.read().offsets();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }


    private void commitOffset(JavaInputDStream<ConsumerRecord<String, Message>> directKafkaStream) {
        directKafkaStream.foreachRDD((JavaRDD<ConsumerRecord<String, Message>> trafficRdd) -> {
            if (!trafficRdd.isEmpty()) {
                OffsetRange[] offsetRanges = ((HasOffsetRanges) trafficRdd.rdd()).offsetRanges();

                CanCommitOffsets canCommitOffsets = (CanCommitOffsets) directKafkaStream.inputDStream();
                canCommitOffsets.commitAsync(offsetRanges, new MessageOffsetCommitCallback());
            }
        });
    }

    private Map<String, Object> getKafkaProperties() {
        Map<String, Object> kafkaProperties = new HashMap<>();
        kafkaProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        kafkaProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        kafkaProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageDeserializer.class);
        kafkaProperties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaTopic);
        kafkaProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, resetType);
        kafkaProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        return kafkaProperties;
    }

    private JavaInputDStream<ConsumerRecord<String, Message>> createStream(JavaStreamingContext streamingContext,
                                                                           Map<String, Object> kafkaProperties,
                                                                           Map<TopicPartition, Long> fromOffsets) {
        List<String> topicSet = Arrays.asList(kafkaTopic);
        if (fromOffsets.isEmpty()) {
            return KafkaUtils.createDirectStream(streamingContext, LocationStrategies.PreferConsistent()
                    , ConsumerStrategies.Subscribe(topicSet, kafkaProperties));
        }
        return KafkaUtils.createDirectStream(streamingContext,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.Subscribe(topicSet, kafkaProperties, fromOffsets));
    }

    private SparkConf getSparkProperties() {
        return new SparkConf()
                .setAppName(sparkAppName)
                .setMaster(sparkMaster)
                .set("spark.cassandra.connection.host", cassandraHost)
                .set("spark.cassandra.connection.port", cassandraPort)
                .set("spark.cassandra.auth.username", cassandraUsername)
                .set("spark.cassandra.auth.password", cassandraPass)
                .set("spark.cassandra.connection.keepAliveMS", cassandraKeepAlive);
    }

}
