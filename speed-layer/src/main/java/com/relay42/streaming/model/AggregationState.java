package com.relay42.streaming.model;
import java.io.Serializable;

public class AggregationState implements Serializable {
    private Long min;
    private Long max;
    private Long med;
    private Long avg;

    public AggregationState() {
    }

    public AggregationState(Long min, Long max, Long med, Long avg) {
        this.min = min;
        this.max = max;
        this.med = med;
        this.avg = avg;
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

}
