package com.relay42.kafka.pub;

import com.relay42.kafka.pub.producer.MessageProducer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KafkaProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaProducerApplication.class, args);

        MessageProducer messageProducer = new MessageProducer();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                messageProducer.produce("device1");
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                messageProducer.produce("device2");
            }
        });
        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
