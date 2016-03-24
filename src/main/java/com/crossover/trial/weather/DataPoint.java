package com.crossover.trial.weather;

import java.io.Serializable;

/**
 * A collected point, including some information about the range of collected values
 */
public class DataPoint implements Serializable {

    private static final long serialVersionUID = -2533695717816373790L;

    /**
     * the mean of the observations
     */
    private double mean = 0.0;

    /**
     * 1st quartile -- useful as a lower bound
     */
    private int first = 0;

    /**
     * 2nd quartile -- median value
     */
    private int second = 0;

    /**
     * 3rd quartile value -- less noisy upper value
     */
    private int third = 0;

    /**
     * the total number of measurements
     */
    private int count = 0;

    public DataPoint() {
    }

    public DataPoint(int first, int second, int mean, int third, int count) {
        this.setFirst(first);
        this.setMean(mean);
        this.setSecond(second);
        this.setThird(third);
        this.setCount(count);
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public int getThird() {
        return third;
    }

    public void setThird(int third) {
        this.third = third;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(mean);
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + first;
        result = 31 * result + second;
        result = 31 * result + third;
        result = 31 * result + count;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataPoint dataPoint = (DataPoint) o;

        if (Double.compare(dataPoint.mean, mean) != 0) return false;
        if (first != dataPoint.first) return false;
        if (second != dataPoint.second) return false;
        if (third != dataPoint.third) return false;

        return count == dataPoint.count;
    }

    @Override
    public String toString() {
        return "DataPoint{" +
                "mean=" + mean +
                ", first=" + first +
                ", second=" + second +
                ", third=" + third +
                ", count=" + count +
                '}';
    }

}
