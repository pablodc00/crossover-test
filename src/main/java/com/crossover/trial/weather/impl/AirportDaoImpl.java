package com.crossover.trial.weather.impl;

import com.crossover.trial.weather.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the airport DAO using simple in-memory data storage
 */
@Service
public class AirportDaoImpl implements AirportDao {

    private final static Logger LOGGER = Logger.getLogger(AirportDaoImpl.class.getName());

    @Override
    public AirportData findAirportData(String iataCode) {
        LOGGER.log(Level.FINE, "Find airport data: " + iataCode);
        if (iataCode == null) return null;
        return Storage.AIRPORT_DATA.get(iataCode);
    }

    @Override
    public Set<AirportData> findNearbyAirports(String iataCode, double radius) {
        LOGGER.log(Level.FINE, "Find nearby airports: " + iataCode + ", " + radius);
        Set<AirportData> result = new HashSet<>();
        if (iataCode == null) return result;

        AirportData ad = Storage.AIRPORT_DATA.get(iataCode);
        if (ad == null) {
            return result;
        }

        for (AirportData a : Storage.AIRPORT_DATA.values()) {
            if (calculateDistance(ad, a) <= radius) {
                result.add(a);
            }
        }

        return result;
    }

    @Override
    public Set<String> getAllAirportCodes() {
        LOGGER.log(Level.FINE, "Get all airport codes");
        return Storage.AIRPORT_DATA.keySet();
    }

    @Override
    public AtmosphericInformation findAtmosphericInformation(String iataCode) {
        LOGGER.log(Level.FINE, "Find atmospheric information: " + iataCode);
        if (iataCode == null) return null;
        return Storage.ATMOSPHERIC_INFORMATION.get(iataCode);
    }

    @Override
    public List<AtmosphericInformation> findAtmosphericInformationNearbyAirport(String iataCode, double radius) {
        LOGGER.log(Level.FINE, "Find atmospheric information near airport: " + iataCode + ", " + radius);
        List<AtmosphericInformation> result = new ArrayList<>();
        if (iataCode == null) return result;

        for (AirportData ad : findNearbyAirports(iataCode, radius)) {
            AtmosphericInformation ai = Storage.ATMOSPHERIC_INFORMATION.get(ad.getIata());
            if (ai != null) {
                result.add(ai);
            }
        }

        return result;
    }

    /**
     * Haversine distance between two airports.
     *
     * @param ad1 airport 1
     * @param ad2 airport 2
     * @return the distance in KM
     */
    private double calculateDistance(AirportData ad1, AirportData ad2) {
        double deltaLat = Math.toRadians(ad2.getLatitude() - ad1.getLatitude());
        double deltaLon = Math.toRadians(ad2.getLongitude() - ad1.getLongitude());
        double a = Math.pow(Math.sin(deltaLat / 2), 2) + Math.pow(Math.sin(deltaLon / 2), 2)
                * Math.cos(ad1.getLatitude()) * Math.cos(ad2.getLatitude());
        double c = 2 * Math.asin(Math.sqrt(a));

        return 6372.8 * c;
    }

    public void updateAtmosphericInformation(String iataCode, String pointType, DataPoint dp) throws WeatherException {
        LOGGER.log(Level.FINE, "Update atmospheric data: " + iataCode + ", " + pointType + ", " + dp);

        if(iataCode == null) throw new WeatherException("Couldn't update atmospheric data. IATA code is null");
        if(Storage.AIRPORT_DATA.get(iataCode) == null) throw new WeatherException("Couldn't update atmospheric data. Unknown IATA code");
        if(pointType == null) throw new WeatherException("Couldn't update atmospheric data. pointType is null");
        if(dp == null) throw new WeatherException("Couldn't update atmospheric data. Data point is null");

        AtmosphericInformation ai = Storage.ATMOSPHERIC_INFORMATION.get(iataCode);
        if (ai == null) {
            ai = new AtmosphericInformation();
            Storage.ATMOSPHERIC_INFORMATION.putIfAbsent(iataCode, ai);
            ai = Storage.ATMOSPHERIC_INFORMATION.get(iataCode);
        }

        if (pointType.equalsIgnoreCase(DataPointType.WIND.name())) {
            if (dp.getMean() >= 0) {
                ai.setWind(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        } else if (pointType.equalsIgnoreCase(DataPointType.TEMPERATURE.name())) {
            if (dp.getMean() >= -50 && dp.getMean() < 100) {
                ai.setTemperature(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        } else if (pointType.equalsIgnoreCase(DataPointType.HUMIDTY.name())) {
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setHumidity(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        } else if (pointType.equalsIgnoreCase(DataPointType.PRESSURE.name())) {
            if (dp.getMean() >= 650 && dp.getMean() < 800) {
                ai.setPressure(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        } else if (pointType.equalsIgnoreCase(DataPointType.CLOUDCOVER.name())) {
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setCloudCover(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        } else if (pointType.equalsIgnoreCase(DataPointType.PRECIPITATION.name())) {
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setPrecipitation(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }

        throw new WeatherException("Couldn't update atmospheric data");
    }

    @Override
    public void saveAirport(AirportData ad) {
        LOGGER.fine("Save airport: " + ad);
        if (ad == null || ad.getIata() == null) {
            LOGGER.severe("Cannot save airport");
            return;
        }
        Storage.AIRPORT_DATA.put(ad.getIata(), ad);
    }

    @Override
    public void deleteAirport(String iataCode) {
        LOGGER.fine("Delete airport: " + iataCode);
        if (iataCode == null) {
            LOGGER.severe("Cannot delete airport");
            return;
        }
        deleteAtmosphericInformation(iataCode);
        Storage.AIRPORT_DATA.remove(iataCode);
    }

    @Override
    public void deleteAtmosphericInformation(String iataCode) {
        LOGGER.fine("Delete atmospheric information: " + iataCode);
        if (iataCode == null)  {
            LOGGER.severe("Cannot delete atmospheric information");
            return;
        }
        Storage.ATMOSPHERIC_INFORMATION.remove(iataCode);
    }

}
