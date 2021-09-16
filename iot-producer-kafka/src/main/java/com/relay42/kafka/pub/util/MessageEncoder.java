package com.relay42.kafka.pub.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.relay42.kafka.pub.model.Message;
import kafka.serializer.Encoder;
import kafka.utils.VerifiableProperties;
import org.apache.log4j.Logger;

public class MessageEncoder implements Encoder<Message> {

    private static final Logger logger = Logger.getLogger(MessageEncoder.class);
    private static ObjectMapper objectMapper = new ObjectMapper();

    public MessageEncoder(VerifiableProperties verifiableProperties) {

    }

    @Override
    public byte[] toBytes(Message iotEvent) {
        try {
            String msg = objectMapper.writeValueAsString(iotEvent);
            logger.info(msg);
            return msg.getBytes();
        } catch (JsonProcessingException e) {
            logger.error("Error in Serialization", e);
        }
        return null;
    }
}
