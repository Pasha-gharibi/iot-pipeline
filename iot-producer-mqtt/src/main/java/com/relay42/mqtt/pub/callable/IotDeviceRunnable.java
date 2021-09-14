package com.relay42.mqtt.pub.callable;

import com.relay42.mqtt.pub.model.Message;
import com.relay42.mqtt.pub.util.PropertyFileReader;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

public class IotDeviceRunnable implements Runnable {
    private static final Logger logger = Logger.getLogger(IotDeviceRunnable.class);

    private final IMqttClient client;
    private  MqttMessage mqttMessage;
    private final Random random = new Random();
    private final String mqtt_server_url;
    private final String mqtt_topic;
    private Message message;
    private String device;

    private final MqttConnectOptions options = new MqttConnectOptions();

    public IotDeviceRunnable(String device) throws Exception {
        this.device = device;
        Properties properties = PropertyFileReader.readPropertyFile();
        this.mqtt_server_url = properties.getProperty("mqtt.server.uri");
        this.mqtt_topic = properties.getProperty("mqtt.server.topic");
        this.client = new MqttClient(mqtt_server_url, this.device);
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (!client.isConnected()) {
                    client.connect(options);
                }
                this.message = new Message.Builder().device(device).date(new Date()).value(random.nextInt(100)).build();
                this.mqttMessage = new MqttMessage(message.toString().getBytes(StandardCharsets.UTF_8));
                this.mqttMessage.setQos(0);
                this.mqttMessage.setRetained(true);
                client.publish(mqtt_topic, mqttMessage);
                logger.info(new StringBuffer("Message sent : ").append(message.toString()));
                Thread.sleep(1000);
            } catch (MqttException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

