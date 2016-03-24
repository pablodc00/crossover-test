package com.crossover.trial.weather.impl;

import com.crossover.trial.weather.*;
import com.hazelcast.core.HazelcastInstance;

import javax.annotation.Resource;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by ekonovalov on 24.03.2016.
 */
public class HazelcastAirportDaoImpl extends AirportDaoImpl {

    private static final Logger LOGGER = Logger.getLogger(HazelcastAirportDaoImpl.class.getName());

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
