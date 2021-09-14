package com.relay42.monitor.repository.impl;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.relay42.monitor.model.Message;
import com.relay42.monitor.repository.MessageRepository;
import com.relay42.monitor.util.QueryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.*;

@Repository
public class MessageRepositoryImpl implements MessageRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageRepositoryImpl.class);

    @Value("${spring.data.cassandra.username}")
    private String username;
    @Value("${spring.data.cassandra.password}")
    private String password;
    @Value("${spring.data.cassandra.keyspace-name}")
    private String keyspace;

    @Override
    public List<Message> search(String[] devices, Timestamp from, Timestamp to) {

        CqlSession cqlSession = CqlSession.builder()
                .withAuthCredentials(username, password)
                .withKeyspace(keyspace).build();

        Map<String, Object> params = new HashMap<>();
        if (devices != null) params.put("devices", devices);
        if (from != null) params.put("from", from);
        if (to != null) params.put("to", to);
        String query = QueryUtil.queryBuilder("SELECT * FROM ks_iot.t_message", null, params);
        LOGGER.info(query);
        List<Message> messages = new ArrayList<>();
        Message message = null;
        ResultSet resultSet = cqlSession.execute(query);
        Iterator<Row> rows = resultSet.iterator();
        while (rows.hasNext()) {
            Row row = rows.next();
            message = new Message();
            message.setDevice(row.getString(0));
            message.setDate(Date.from(row.getInstant(1)));
            message.setValue(row.getLong(2));
            messages.add(message);
        }
        cqlSession.close();
        return messages;
    }


    @Override
    public Long average(String[] devices, Timestamp from, Timestamp to) {
        CqlSession cqlSession = CqlSession.builder()
                .withAuthCredentials(username, password)
                .withKeyspace(keyspace).build();

        Map<String, Object> params = new HashMap<>();
        if (devices != null) params.put("devices", devices);
        if (from != null) params.put("from", from);
        if (to != null) params.put("to", to);
        String query = QueryUtil.queryBuilder("SELECT avg(c_value) FROM ks_iot.t_message", null, params);
        LOGGER.info(query);
        ResultSet resultSet = cqlSession.execute(query);
        cqlSession.close();
        return resultSet.one().getLong(0);
    }

    @Override
    public Long minimum(String[] devices, Timestamp from, Timestamp to) {
        CqlSession cqlSession = CqlSession.builder()
                .withAuthCredentials(username, password)
                .withKeyspace(keyspace).build();

        Map<String, Object> params = new HashMap<>();
        if (devices != null) params.put("devices", devices);
        if (from != null) params.put("from", from);
        if (to != null) params.put("to", to);
        String query = QueryUtil.queryBuilder("SELECT min(c_value) FROM ks_iot.t_message", null, params);
        LOGGER.info(query);
        ResultSet resultSet = cqlSession.execute(query);
        cqlSession.close();
        return resultSet.one().getLong(0);
    }

    @Override
    public Long maximum(String[] devices, Timestamp from, Timestamp to) {
        CqlSession cqlSession = CqlSession.builder()
                .withAuthCredentials(username, password)
                .withKeyspace(keyspace).build();

        Map<String, Object> params = new HashMap<>();
        if (devices != null) params.put("devices", devices);
        if (from != null) params.put("from", from);
        if (to != null) params.put("to", to);
        String query = QueryUtil.queryBuilder("SELECT max(c_value) FROM ks_iot.t_message", null, params);
        LOGGER.info(query);
        ResultSet resultSet = cqlSession.execute(query);
        cqlSession.close();
        return resultSet.one().getLong(0);
    }

    @Override
    public Double median(String[] devices, Timestamp from, Timestamp to) {
        CqlSession cqlSession = CqlSession.builder()
                .withAuthCredentials(username, password)
                .withKeyspace(keyspace).build();

        Map<String, Object> params = new HashMap<>();
        if (devices != null) params.put("devices", devices);
        if (from != null) params.put("from", from);
        if (to != null) params.put("to", to);
        String query = QueryUtil.queryBuilder("SELECT * FROM ks_iot.t_message", null, params);
        LOGGER.info(query);
        ResultSet resultSet = cqlSession.execute(query);
        List<Long> values = new ArrayList<>();
        Iterator<Row> rows = resultSet.iterator();
        while (rows.hasNext()) {
            Row row = rows.next();
            values.add(row.getLong(2));
        }
        cqlSession.close();
        return QueryUtil.Median(values.stream().mapToLong(l -> l).toArray());
    }


}
