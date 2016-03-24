package com.crossover.trial.weather;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Holder object for performance data
 */
public class RequestFrequencyData {

    /**
     * size of actual atmospheric information collection
     */
    @JsonProperty("datasize")
    private long dataSize;

    /**
     * frequency of queries of atmospheric information near airports
     */
    @JsonProperty("iata_freq")
    private Map<String, Double> airportFrequency = new HashMap<>();

    /**
     * frequency of queries for atmospheric information near airports
     */
    @JsonProperty("radius_freq")
    private int[] radiusFrequency;

    public long getDataSize() {
        return dataSize;
    }

    public void setDataSize(long dataSize) {
        this.dataSize = dataSize;
    }

    public Map<String, Double> getAirportFrequency() {
        return airportFrequency;
    }

    public void setAirportFrequency(Map<String, Double> airportFrequency) {
        this.airportFrequency = airportFrequency;
    }

    public int[] getRadiusFrequency() {
        return radiusFrequency;
    }

    public void setRadiusFrequency(int[] radiusFrequency) {
        this.radiusFrequency = radiusFrequency;
    }

}
