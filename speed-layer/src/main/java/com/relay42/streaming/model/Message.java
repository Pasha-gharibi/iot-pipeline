package com.relay42.streaming.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class Message implements Serializable {

    private static final long serialVersionUID = 1L;
    private String device;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "UTC")
    private Date date;
    private Integer value;
    private Map<String, String> metaData;


    public Message() {
    }

    @Override
    public String toString() {
        return "Message{" +
                "device='" + device +
                ", date=" + date +
                ", value=" + value +
                '}';
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Map<String, String> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<String, String> metaData) {
        this.metaData = metaData;
    }
}
