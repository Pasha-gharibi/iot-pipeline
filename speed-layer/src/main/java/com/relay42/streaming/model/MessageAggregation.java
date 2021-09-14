package com.relay42.streaming.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.sql.Timestamp;

public class MessageAggregation implements Serializable {

    private String device;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Timestamp date;
    private Long min;
    private Long max;
    private Long med;
    private Long avg;

    public MessageAggregation() {
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public Long getMin() {
        return min;
    }

    public void setMin(Long min) {
        this.min = min;
    }

    public Long getMax() {
        return max;
    }

    public void setMax(Long max) {
        this.max = max;
    }

    public Long getMed() {
        return med;
    }

    public void setMed(Long med) {
        this.med = med;
    }

    public Long getAvg() {
        return avg;
    }

    public void setAvg(Long avg) {
        this.avg = avg;
    }

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}
}
