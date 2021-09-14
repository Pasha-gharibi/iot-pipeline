package com.relay42.monitor.repository;

import com.relay42.monitor.model.Message;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface MessageRepository {

    List<Message> search(String[] devices, Timestamp from, Timestamp to);

    Long average(String[] devices, Timestamp from, Timestamp to);

    Long minimum(String[] devices, Timestamp from, Timestamp to);

    Long maximum(String[] devices, Timestamp from, Timestamp to);

    Double median(String[] devices, Timestamp from, Timestamp to);
}
