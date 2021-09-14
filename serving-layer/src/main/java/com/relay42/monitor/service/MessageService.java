package com.relay42.monitor.service;

import com.relay42.monitor.model.Message;

import java.util.Date;
import java.util.List;


public interface MessageService {

    List<Message> search(List<String> devices, Date from, Date to);

    Long average(List<String> devices, Date from, Date to);

    Long minimum(List<String> devices, Date from, Date to);

    Long maximum(List<String> devices, Date from, Date to);

    Double median(List<String> devices, Date from, Date to);
}
