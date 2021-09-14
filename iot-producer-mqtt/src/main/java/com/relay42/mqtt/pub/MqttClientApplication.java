package com.relay42.mqtt.pub;

import com.relay42.mqtt.pub.callable.IotDeviceRunnable;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;


@SpringBootApplication
public class MqttClientApplication {

        public static void main(String[] args) throws Exception {
        new SpringApplicationBuilder(MqttClientApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);

        Thread t1 = new Thread(new IotDeviceRunnable("client1"));
        Thread t2 = new Thread(new IotDeviceRunnable("client2"));
        Thread t3 = new Thread(new IotDeviceRunnable("client3"));
        Thread t4 = new Thread(new IotDeviceRunnable("client4"));
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        try {
            t1.join();
            t2.join();
            t3.join();
            t4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


