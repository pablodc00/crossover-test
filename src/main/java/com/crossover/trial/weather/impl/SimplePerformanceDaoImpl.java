package com.crossover.trial.weather.impl;

import com.crossover.trial.weather.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the performance DAO using simple in-memory data storage
 */
@Service
public class SimplePerformanceDaoImpl implements PerformanceDao {

    private final static Logger LOGGER = Logger.getLogger(SimpleAirportDaoImpl.class.getName());

    @Resource
    private AirportDao airportDao;

    @Override
    public void updateRequestFrequency(String iata, Double radius) {
        LOGGER.log(Level.FINE, "Update request frequency: " + iata + ", " + radius);

        AirportData airportData = airportDao.findAirportData(iata);

        if (airportData != null) {
            AtomicInteger i = Storage.REQUEST_FREQUENCY.get(airportData);
            if (i == null) {
                Storage.REQUEST_FREQUENCY.putIfAbsent(airportData, new AtomicInteger(0));
                i = Storage.REQUEST_FREQUENCY.get(airportData);

            }
            i.incrementAndGet();

            // radius cannot be less than zero
            if (radius < 0) radius = 0d;
            // all request with radius above 1000 to one counter. It's unusual to request radius > 1000.
            if (radius > 1000) radius = 1000d;

            i = Storage.RADIUS_FREQUENCY.get(radius);
            if (i == null) {
                Storage.RADIUS_FREQUENCY.putIfAbsent(radius, new AtomicInteger(0));
                i = Storage.RADIUS_FREQUENCY.get(radius);
            }
            i.incrementAndGet();
        }
    }

    @Override
    public RequestFrequencyData getPerformanceData() {
        LOGGER.log(Level.FINE, "Get performance data");

        RequestFrequencyData data = new RequestFrequencyData();

        int dataSize = 0;
        for (AtmosphericInformation ai : Storage.ATMOSPHERIC_INFORMATION.values()) {
            // we only count recent readings
            if (ai.getCloudCover() != null
                    || ai.getHumidity() != null
                    || ai.getPressure() != null
                    || ai.getPrecipitation() != null
                    || ai.getTemperature() != null
                    || ai.getWind() != null) {
                // updated in the last day
                if (ai.getLastUpdateTime() > System.currentTimeMillis() - 86400000) {
                    dataSize++;
                }
            }
        }

        data.setDataSize(dataSize);

        Map<String, Double> freq = new HashMap<>();

        int count = 0;
        for (AtomicInteger i : Storage.REQUEST_FREQUENCY.values()) {
            count += i.get();
        }

        if (count > 0) {
            for (AirportData ad : Storage.AIRPORT_DATA.values()) {
                double frac = (double) Storage.REQUEST_FREQUENCY.getOrDefault(ad, new AtomicInteger(0)).intValue() / count;
                freq.put(ad.getIata(), frac);
            }
        }

        data.setAirportFrequency(freq);

        int m = Storage.RADIUS_FREQUENCY.keySet().stream()
                .max(Double::compare)
                .orElse(0d).intValue();

        if (m < 0) m = 0;
        if (m > 1000) m = 1000;

        int[] hist = new int[m + 1];

        for (Map.Entry<Double, AtomicInteger> e : Storage.RADIUS_FREQUENCY.entrySet()) {
            int i = e.getKey().intValue();
            hist[i] = e.getValue().intValue();
        }

        data.setRadiusFrequency(hist);

        LOGGER.log(Level.FINE, "Performance data : " + data);

        return data;
    }

    @Override
    public void clear() {
        Storage.RADIUS_FREQUENCY.clear();
        Storage.REQUEST_FREQUENCY.clear();
    }

}
