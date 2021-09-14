package com.relay42.batching.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

public class MessageAggregation implements Serializable{

	private String device;
	private Long min;
	private Long max;
	private long med;
	private long avg;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone="MST")
	private Date date;

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

	public long getMed() {
		return med;
	}

	public void setMed(long med) {
		this.med = med;
	}

	public long getAvg() {
		return avg;
	}

	public void setAvg(long avg) {
		this.avg = avg;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
