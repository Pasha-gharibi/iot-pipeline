package com.relay42.kafka.pub.producer;

import com.relay42.kafka.pub.model.Message;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

public class MessageProducer {


    private static final Logger logger = Logger.getLogger(MessageProducer.class);
    private Producer kafkaProducer;
    private String topic;

    public MessageProducer() {
        initKafka();
    }

    private void initKafka() {
        ClassPathResource resource = new ClassPathResource("/application.properties");
        try {
            Properties properties = PropertiesLoaderUtils.loadProperties(resource);
            this.topic = properties.getProperty("kafka.topic");
            this.kafkaProducer = new Producer(new ProducerConfig(properties));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void produce(String device) {
        Random random = new Random();
        while (true) {
            Message message = new Message.Builder()
                    .device(device)
                    .date(new Date())
                    .value(random.nextInt(100))
                    .build();
            logger.info("Message sent: " + message);
            kafkaProducer.send(new KeyedMessage<>(topic, message));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
