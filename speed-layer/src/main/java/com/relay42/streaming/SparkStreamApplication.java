package com.relay42.streaming;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SparkStreamApplication {

    @Autowired

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(SparkStreamApplication.class, args);
        try {
            StreamingOrchestrator streamingOrchestrator = applicationContext.getBean(StreamingOrchestrator.class);
            streamingOrchestrator.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
