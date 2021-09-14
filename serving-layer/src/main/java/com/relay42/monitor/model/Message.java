package com.relay42.monitor.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;

@Table("t_message")
public class Message {


    @PrimaryKeyColumn(name = "c_device", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String device;
    @PrimaryKeyColumn(name = "c_value", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private Long value;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "UTC")
    @Column(value = "c_date")
    private Date date;

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
