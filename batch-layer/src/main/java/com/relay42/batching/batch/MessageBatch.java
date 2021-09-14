package com.relay42.batching.batch;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.relay42.batching.model.Message;
import com.relay42.batching.model.MessageAggregation;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.springframework.stereotype.Component;
import scala.Tuple2;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class MessageBatch {
    private static final Logger logger = Logger.getLogger(MessageBatch.class);

    public MessageBatch() {
    }

    private static void persistInServingLayer(JavaRDD<MessageAggregation> trafficDStream) {
        Map<String, String> columnNameMappings = new HashMap<String, String>();
        columnNameMappings.put("device", "c_device");
        columnNameMappings.put("date", "c_date");
        columnNameMappings.put("min", "c_min");
        columnNameMappings.put("max", "c_max");
        columnNameMappings.put("med", "c_med");
        columnNameMappings.put("avg", "c_avg");

        CassandraJavaUtil.javaFunctions(trafficDStream).writerBuilder("ks_iot", "t_aggregation",
                CassandraJavaUtil.mapToRow(MessageAggregation.class, columnNameMappings)
        ).saveToCassandra();
    }

    private static final MessageAggregation map(Tuple2<String, MessageAggregation> tuple) {
        MessageAggregation aggregation = new MessageAggregation();
        aggregation.setDevice(tuple._1());
        aggregation.setDate(new Date());
        aggregation.setAvg((tuple._2().getAvg()));
        aggregation.setMax(tuple._2().getMax());
        aggregation.setMin(tuple._2().getMin());
        aggregation.setMed(tuple._2().getMed());
        return aggregation;
    }


    public void run(JavaRDD<Message> messageRDD) {
        JavaPairRDD<String, MessageAggregation> countDStreamPair =
                messageRDD.mapToPair(m -> new Tuple2<>(m.getDevice(), new MessageAggregation()));
        JavaRDD<MessageAggregation> messageAggregationRDD = countDStreamPair.map(MessageBatch::map);
        persistInServingLayer(messageAggregationRDD);
    }

}
