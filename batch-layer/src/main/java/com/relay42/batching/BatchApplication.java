package com.relay42.batching;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class BatchApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(BatchApplication.class, args);
        try {
            BatchRunner batchRunner = applicationContext.getBean(BatchRunner.class);
            batchRunner.run();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
