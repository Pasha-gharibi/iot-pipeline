package com.relay42.batching;


import com.relay42.batching.batch.MessageBatch;
import com.relay42.batching.model.Message;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BatchRunner {

    private static final Logger logger = Logger.getLogger(BatchRunner.class);
    private final String hdfsFile;
    private final String cassandraHost;
    private final String cassandraPort;
    private final String cassandraUsername;
    private final String cassandraPass;
    private final String cassandraKeepAlive;
    private final String sparkAppName;
    private final String sparkMaster;

    @Autowired
    private MessageBatch messageBatch;

    public BatchRunner(@Value("${hadoop.server.hdfs}") String hdfsFile,
                       @Value("${cassandra.server.host}") String cassandraHost,
                       @Value("${cassandra.server.port}") String cassandraPort,
                       @Value("${cassandra.server.username}") String cassandraUsername,
                       @Value("${cassandra.server.password}") String cassandraPass,
                       @Value("${cassandra.server.keepAliveMS}") String cassandraKeepAlive,
                       @Value("${spark.server.app.name}") String sparkAppName,
                       @Value("${spark.server.master}") String sparkMaster) {

        this.hdfsFile = hdfsFile;
        this.cassandraHost = cassandraHost;
        this.cassandraPort = cassandraPort;
        this.cassandraUsername = cassandraUsername;
        this.cassandraPass = cassandraPass;
        this.cassandraKeepAlive = cassandraKeepAlive;
        this.sparkAppName = sparkAppName;
        this.sparkMaster = sparkMaster;
    }

    private static Message map(Row row) {
        return new Message.Builder()
                .device(row.getString(0))
                .value(row.getInt(1))
                .date(row.getDate(2))
                .build();
    }

    public void run() {
        var conf = getSparkProperties();
        var sparkSession = SparkSession.builder().config(conf).getOrCreate();
        var dataFrame = sparkSession.read().parquet(this.hdfsFile + "");
        var rdd = dataFrame.javaRDD().map(BatchRunner::map);
        logger.info("Message batch started");
        messageBatch.run(rdd);
        sparkSession.close();
        sparkSession.stop();
    }

    private SparkConf getSparkProperties() {
        return new SparkConf()
                .setAppName(sparkAppName)
                .setMaster(sparkMaster)
                .set("spark.cassandra.connection.host", cassandraHost)
                .set("spark.cassandra.connection.port", cassandraPort)
                .set("spark.cassandra.auth.username", cassandraUsername)
                .set("spark.cassandra.auth.password", cassandraPass)
                .set("spark.cassandra.connection.keep_alive_ms", cassandraKeepAlive);
    }

}

