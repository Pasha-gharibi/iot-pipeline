package com.relay42.streaming.processor;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.relay42.streaming.model.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.kafka010.HasOffsetRanges;
import org.apache.spark.streaming.kafka010.OffsetRange;

import java.io.Serializable;
import java.util.*;

import static com.datastax.spark.connector.japi.CassandraStreamingJavaUtil.javaFunctions;

public class MessageProcessor implements Serializable {

    private static final Logger logger = Logger.getLogger(MessageProcessor.class);

    final JavaDStream<ConsumerRecord<String, Message>> inputStream;
    private JavaDStream<Message> messageStream;

    public MessageProcessor(JavaDStream<ConsumerRecord<String, Message>> inputStream) {
        this.inputStream = inputStream;
    }

    private static JavaRDD<Message> map(JavaRDD<ConsumerRecord<String, Message>> item) {
        OffsetRange[] offsetRanges= ((HasOffsetRanges) item.rdd()).offsetRanges();
        return item.mapPartitionsWithIndex(partitionPerDay(offsetRanges), true);
    }

    private static Function2<Integer, Iterator<ConsumerRecord<String, Message>>, Iterator<Message>> partitionPerDay(final OffsetRange[] offsetRanges) {
        return (index, items) -> {
            List<Message> list = new ArrayList<>();
            while (items.hasNext()) {
                ConsumerRecord<String, Message> next = items.next();
                Message message = next.value();
                Map<String, String> meta = new HashMap<>();
                meta.put("topic", offsetRanges[index].topic());
                meta.put("fromOffset", "" + offsetRanges[index].fromOffset());
                meta.put("kafkaPartition", "" + offsetRanges[index].partition());
                meta.put("untilOffset", "" + offsetRanges[index].untilOffset());
                meta.put("dayOfWeek", "" + getDayNumberOld(message.getDate()));
                message.setMetaData(meta);
                list.add(message);
            }
            return list.iterator();
        };
    }

    public static int getDayNumberOld(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public MessageProcessor map() {
        this.messageStream = inputStream.transform(MessageProcessor::map);
        return this;
    }

    public MessageProcessor persistToBatchLayer(final SparkSession sql, final String file) {
        logger.info("Saving in hdfs");
        messageStream.foreachRDD(rdd -> {
            if (rdd.isEmpty()) {
                return;
            }
            Dataset<Row> dataFrame = sql.createDataFrame(rdd, Message.class);
            Dataset<Row> dfStore = dataFrame.selectExpr(
                    "device",
                    "date",
                    "value",
                    "metaData.fromOffset as fromOffset",
                    "metaData.untilOffset as untilOffset",
                    "metaData.kafkaPartition as kafkaPartition",
                    "metaData.topic as topic",
                    "metaData.dayOfWeek as dayOfWeek");
            dfStore.printSchema();
            dfStore.write()
                    .partitionBy("topic", "kafkaPartition", "dayOfWeek")
                    .mode(SaveMode.Append)
                    .parquet(file);
        });
        return this;
    }

    public MessageProcessor persistInServingLayer() {
        this.messageStream.foreachRDD(rdd->rdd.collect().forEach(m ->
                System.out.println(m.toString())
        ));
        HashMap<String, String> columnNameMappings = new HashMap<>();
        columnNameMappings.put("device", "c_device");
        columnNameMappings.put("date", "c_date");
        columnNameMappings.put("value", "c_value");
        CassandraJavaUtil.mapToRow(Message.class, columnNameMappings);
        javaFunctions(this.messageStream).writerBuilder("ks_iot","t_message",
                CassandraJavaUtil.mapToRow(Message.class, columnNameMappings)).saveToCassandra();

        return this;
    }

    public MessageProcessor cache() {
        this.messageStream.cache();
        return this;
    }

//    private static MessageAggregation map(Tuple2<String, AggregationState> tuple) {
//        MessageAggregation messageAggregation = new MessageAggregation();
//        messageAggregation.setDevice(tuple._1);
//        messageAggregation.setDate(new Timestamp(new Date().getTime()));
//        messageAggregation.setAvg(tuple._2.getAvg());
//        messageAggregation.setMed(tuple._2.getMed());
//        messageAggregation.setMin(tuple._2.getMin());
//        messageAggregation.setMax(tuple._2.getMax());
//        return messageAggregation;
//    }

//    private static Tuple2<String, AggregationState> updateState(String key,
//                                                                org.apache.spark.api.java.Optional<AggregationState> currentStateOption,
//                                                                State<AggregationState> state) {
//        AggregationState currentState = currentStateOption.get();
//        currentState = currentState == null ? new AggregationState() : currentState;
//        if (state.exists()) {
//            currentState.setAvg((state.get().getAvg() + currentState.getAvg()) / 2);
//            currentState.setMax(Math.max(state.get().getAvg(), currentState.getAvg()));
//            currentState.setMin(Math.min(state.get().getMin(), currentState.getMin()));
//            currentState.setMed(state.get().getMed() + currentState.getMed() / 2);
//        }
//        Tuple2<String, AggregationState> total = new Tuple2<>(key, currentState);
//        state.update(currentState);
//        return total;
//    }



//    public MessageProcessor calculateAggregations() {
//        StateSpec<String, AggregationState, AggregationState, Tuple2<String, AggregationState>> stateSpec =
//                StateSpec.function(MessageProcessor::updateState).timeout(Durations.seconds(3600));
//
//        JavaDStream<MessageAggregation> aggregationsJavaDStream = messageStream
//                .mapToPair(m -> new Tuple2<>(m.getDevice(), new AggregationState()))
//                .mapWithState(stateSpec)
//                .map(MessageProcessor::map);
//        persistInServingLayer(aggregationsJavaDStream);
//        return this;
//    }



//    private void persistInServingLayer(final JavaDStream<MessageAggregation> trafficDStream) {
//
//        HashMap<String, String> columnNameMappings = new HashMap<>();
//        columnNameMappings.put("device", "c_device");
//        columnNameMappings.put("date", "c_date");
//        columnNameMappings.put("min", "c_min");
//        columnNameMappings.put("max", "c_max");
//        columnNameMappings.put("med", "c_med");
//        columnNameMappings.put("avg", "c_avg");
//        javaFunctions(trafficDStream).writerBuilder("ks_iot", "t_aggregation",
//                CassandraJavaUtil.mapToRow(MessageAggregation.class, columnNameMappings)).saveToCassandra();
//    }


//    private Long median(Long[] list) {
//        Arrays.sort(list);
//        Long median;
//        if (list.length % 2 == 0) {
//            median = (list[list.length / 2] + list[list.length / 2 - 1]) / 2;
//        } else {
//            median = list[list.length / 2];
//        }
//        return median;
//    }

//    public MessageProcessor metadata(SparkConf sparkConf) {
//        CassandraConnector connector = CassandraConnector.apply(sparkConf);
//        connector.log();
//        try (CqlSession session = connector.openSession()) {
//            session.execute("DROP KEYSPACE IF EXISTS ks_iot");
//            session.execute("CREATE KEYSPACE ks_iot WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}");
//            session.execute("CREATE TABLE ks_iot.t_aggregation (c_device text , c_date TIMESTAMP, c_min bigint, c_max bigint, c_avg bigint, c_med bigint ,PRIMARY KEY (c_device,c_date))");
//        }
//        return this;
//    }

}
