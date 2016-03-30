package com.crossover.trial.weather.impl;

import com.crossover.trial.weather.AirportData;
import com.crossover.trial.weather.AtmosphericInformation;
import com.hazelcast.core.HazelcastInstance;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Implementation of the airport DAO using Hazelcast framework.
 * <p>
 * It's an example of the implementation that can be used in production. It's easy to
 */
public class HazelcastAirportDaoImpl extends AirportDaoImpl {

    @Resource
    private HazelcastInstance hazelcastInstance;

    @Override
    protected Map<String, AirportData> getAirportDataStorage() {
        return hazelcastInstance.getMap("AIRPORT_DATA");
    }

    @Override
    protected Map<String, AtmosphericInformation> getAtmosphericInformationDataStorage() {
        return hazelcastInstance.getMap("ATMOSPHERIC_INFORMATION");
    }

}
