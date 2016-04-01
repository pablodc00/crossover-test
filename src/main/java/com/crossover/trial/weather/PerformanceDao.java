package com.crossover.trial.weather;

/**
 * DAO for performance data
 */
@Deprecated
public interface PerformanceDao {

    /**
     * Create performance data object
     *
     * @return a holder object for performance data
     */
    RequestFrequencyData getPerformanceData();

    /**
     * Record information about how often requests are made
     *
     * @param iata   an iata code
     * @param radius query radius
     */
    void updateRequestFrequency(String iata, Double radius);

    /**
     * Reset performance data
     */
    void clear();

}
