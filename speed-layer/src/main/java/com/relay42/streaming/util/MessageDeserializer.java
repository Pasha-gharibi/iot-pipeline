package com.relay42.streaming.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.relay42.streaming.model.Message;
import org.apache.kafka.common.serialization.Deserializer;

public class MessageDeserializer implements Deserializer<Message> {

    private static final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public Message deserialize(String s, byte[] bytes) {
        try {
            return objectMapper.readValue(bytes, Message.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    @Override
//    public void configure(Map<String, ?> map, boolean b) {
//
//    }
//    @Override
//    public void close() {
//
//    }
}
