package com.relay42.monitor.service.impl;

import com.relay42.monitor.model.Message;
import com.relay42.monitor.repository.MessageRepository;
import com.relay42.monitor.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    MessageRepository messageRepository;

    @Override
    public List<Message> search(List<String> devices, Date from, Date to) {
        return messageRepository.search(
                devices.size() != 0 ? devices.toArray(new String[0]) : null,
                from != null ? new Timestamp(from.getTime()) : null,
                to != null ? new Timestamp(to.getTime()) : null);
    }

    @Override
    public Long average(List<String> devices, Date from, Date to) {
        return messageRepository.average(
                devices.size() != 0 ? devices.toArray(new String[0]) : null,
                from != null ? new Timestamp(from.getTime()) : null,
                to != null ? new Timestamp(to.getTime()) : null);
    }

    @Override
    public Long minimum(List<String> devices, Date from, Date to) {
        return messageRepository.minimum(
                devices.size() != 0 ? devices.toArray(new String[0]) : null,
                from != null ? new Timestamp(from.getTime()) : null,
                to != null ? new Timestamp(to.getTime()) : null);
    }

    @Override
    public Long maximum(List<String> devices, Date from, Date to) {
        return messageRepository.maximum(
                devices.size() != 0 ? devices.toArray(new String[0]) : null,
                from != null ? new Timestamp(from.getTime()) : null,
                to != null ? new Timestamp(to.getTime()) : null);
    }

    @Override
    public Double median(List<String> devices, Date from, Date to) {
        return messageRepository.median(
                devices.size() != 0 ? devices.toArray(new String[0]) : null,
                from != null ? new Timestamp(from.getTime()) : null,
                to != null ? new Timestamp(to.getTime()) : null);
    }
}
