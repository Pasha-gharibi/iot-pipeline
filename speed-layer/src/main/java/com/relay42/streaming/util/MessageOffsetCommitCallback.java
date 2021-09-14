package com.relay42.streaming.util;

import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.TopicPartition;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.Map;

public final class MessageOffsetCommitCallback implements OffsetCommitCallback, Serializable {

    private static final Logger log = Logger.getLogger(MessageOffsetCommitCallback.class);

    @Override
    public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
        log.info(String.format("{0} | {1}", offsets, exception));
    }

}
