package com.crossover.trial.weather.impl;

import com.crossover.trial.weather.AirportData;
import com.crossover.trial.weather.AtmosphericInformation;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementation of the airport DAO using simple in-memory data storage
 */
@Service
public class SimpleAirportDaoImpl extends AirportDaoImpl {

    @Override
    protected Map<String, AirportData> getAirportDataStorage() {
        return Storage.AIRPORT_DATA;
    }

    @Override
    protected Map<String, AtmosphericInformation> getAtmosphericInformationDataStorage() {
        return Storage.ATMOSPHERIC_INFORMATION;
    }

}
