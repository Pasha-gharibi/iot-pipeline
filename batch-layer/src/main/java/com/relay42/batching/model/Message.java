package com.relay42.batching.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.sql.Date;
import java.util.Map;

public class Message implements Serializable {

    private String device;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;
    private Integer value;
    private Map<String, String> metaData;


    @Override
    public String toString() {
        return "Message{" +
                "device='" + device +
                ", date=" + date +
                ", value=" + value +
                '}';
    }

    public Message() {
    }


    public String getDevice() {
        return device;
    }

    public Date getDate() {
        return date;
    }

    public Integer getValue() {
        return value;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public void setDate(Date date) {
        this.date = date;
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



    public static class Builder {

        private String device;
        private Date date;
        private Integer value;

        public Builder() {
        }

        Builder(String device, Date date, Integer value) {
            this.device = device;
            this.date = date;
            this.value = value;
        }

        public Builder device(String device){
            this.device = device;
            return Builder.this;
        }

        public Builder date(Date date){
            this.date = date;
            return Builder.this;
        }

        public Builder value(Integer value){
            this.value = value;
            return Builder.this;
        }

        public Message build() {
            if(this.device == null){
                throw new NullPointerException("The property \"device\" is null. "
                        + "Please set the value by \"device()\". "
                        + "The properties \"device\", \"date\" and \"value\" are required.");
            }
            if(this.date == null){
                throw new NullPointerException("The property \"date\" is null. "
                        + "Please set the value by \"date()\". "
                        + "The properties \"device\", \"date\" and \"value\" are required.");
            }
            if(this.value == null){
                throw new NullPointerException("The property \"value\" is null. "
                        + "Please set the value by \"value()\". "
                        + "The properties \"device\", \"date\" and \"value\" are required.");
            }

            return new Message(this);
        }
    }

    private Message(Builder builder) {
        this.device = builder.device;
        this.date = builder.date;
        this.value = builder.value;
    }

    public void doSomething() {
        // do something
    }


}
