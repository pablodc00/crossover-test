package com.crossover.trial.weather.impl;

import com.crossover.trial.weather.AirportData;
import com.crossover.trial.weather.AtmosphericInformation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple in-memory data storage. Production system has to implement more reliable storage.
 */
public class Storage {

    /**
     * All known airports
     */
    public static Map<String, AirportData> AIRPORT_DATA = new ConcurrentHashMap<>();

    /**
     * Atmospheric information for each airport
     */
    public static Map<String, AtmosphericInformation> ATMOSPHERIC_INFORMATION = new ConcurrentHashMap<>();

    /**
     * Internal performance counters to better understand most requested information, this map can be improved but
     * for now provides the basis for future performance optimizations. Due to the stateless deployment architecture
     * we don't want to write this to disk, but will pull it off using a REST request and aggregate with other
     * performance metrics {@link com.crossover.trial.weather.RestWeatherQueryEndpoint}
     */
    public static Map<AirportData, AtomicInteger> REQUEST_FREQUENCY = new ConcurrentHashMap<>();
    public static Map<Double, AtomicInteger> RADIUS_FREQUENCY = new ConcurrentHashMap<>();

}
