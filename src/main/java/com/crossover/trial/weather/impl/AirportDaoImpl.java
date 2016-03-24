package com.crossover.trial.weather.impl;

import com.crossover.trial.weather.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ekonovalov on 24.03.2016.
 */
public abstract class AirportDaoImpl implements AirportDao {

    private static final Logger LOGGER = Logger.getLogger(AirportDaoImpl.class.getName());

    protected abstract Map<String, AirportData> getAirportDataStorage();
    protected abstract Map<String, AtmosphericInformation> getAtmosphericInformationDataStorage();

    @Override
    public AirportData findAirportData(String iataCode) {
        LOGGER.log(Level.FINE, "Find airport data: " + iataCode);
        if (iataCode == null) return null;
        return getAirportDataStorage().get(iataCode);
    }

    @Override
    public Set<AirportData> findNearbyAirports(String iataCode, double radius) {
        LOGGER.log(Level.FINE, "Find nearby airports: " + iataCode + ", " + radius);
        Set<AirportData> result = new HashSet<>();
        if (iataCode == null) return result;

        AirportData ad = getAirportDataStorage().get(iataCode);
        if (ad == null) {
            return result;
        }

        for (AirportData a : getAirportDataStorage().values()) {
            if (calculateDistance(ad, a) <= radius) {
                result.add(a);
            }
        }

        return result;
    }

    @Override
    public Set<String> getAllAirportCodes() {
        LOGGER.log(Level.FINE, "Get all airport codes");
        return getAirportDataStorage().keySet();
    }

    @Override
    public AtmosphericInformation findAtmosphericInformation(String iataCode) {
        LOGGER.log(Level.FINE, "Find atmospheric information: " + iataCode);
        if (iataCode == null) return null;
        return getAtmosphericInformationDataStorage().get(iataCode);
    }

    @Override
    public List<AtmosphericInformation> findAtmosphericInformationNearbyAirport(String iataCode, double radius) {
        LOGGER.log(Level.FINE, "Find atmospheric information near airport: " + iataCode + ", " + radius);
        List<AtmosphericInformation> result = new ArrayList<>();
        if (iataCode == null) return result;

        for (AirportData ad : findNearbyAirports(iataCode, radius)) {
            AtmosphericInformation ai = getAtmosphericInformationDataStorage().get(ad.getIata());
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

    public void updateAtmosphericInformation(String iataCode, String pointType, DataPoint dp) {
        LOGGER.log(Level.FINE, "Update atmospheric data: " + iataCode + ", " + pointType + ", " + dp);

        if(iataCode == null) throw new IllegalArgumentException("IATA code is null");
        if(getAirportDataStorage().get(iataCode) == null) throw new IllegalArgumentException("Unknown IATA code");
        if(pointType == null) throw new IllegalArgumentException("pointType is null");
        if(dp == null) throw new IllegalArgumentException("Data point is null");

        AtmosphericInformation oldValue = getAtmosphericInformationDataStorage().get(iataCode);
        if (oldValue == null) {
            oldValue = new AtmosphericInformation();
            getAtmosphericInformationDataStorage().putIfAbsent(iataCode, oldValue);
            oldValue = getAtmosphericInformationDataStorage().get(iataCode);
        }

        while(true) {
            AtmosphericInformation newValue = new AtmosphericInformation(oldValue.getTemperature(), oldValue.getHumidity(), oldValue.getWind(), oldValue.getPrecipitation(), oldValue.getPressure(), oldValue.getCloudCover());
            updateAtmosphericInformation(newValue, pointType, dp);
            if(getAtmosphericInformationDataStorage().replace(iataCode, oldValue, newValue)) break;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, "", e);
            }
        }
    }

    protected void updateAtmosphericInformation(AtmosphericInformation ai, String pointType, DataPoint dp) {
        if (pointType.equalsIgnoreCase(DataPointType.WIND.name())) {
            if (dp.getMean() >= 0) {
                ai.setWind(dp);
            }
            else throw new IllegalArgumentException("Wrong parameter " + pointType + " = " + dp.getMean());
        } else if (pointType.equalsIgnoreCase(DataPointType.TEMPERATURE.name())) {
            if (dp.getMean() >= -50 && dp.getMean() < 100) {
                ai.setTemperature(dp);
            }
            else throw new IllegalArgumentException("Wrong parameter " + pointType + " = " + dp.getMean());
        } else if (pointType.equalsIgnoreCase(DataPointType.HUMIDTY.name())) {
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setHumidity(dp);
            }
            else throw new IllegalArgumentException("Wrong parameter " + pointType + " = " + dp.getMean());
        } else if (pointType.equalsIgnoreCase(DataPointType.PRESSURE.name())) {
            if (dp.getMean() >= 650 && dp.getMean() < 800) {
                ai.setPressure(dp);
            }
            else throw new IllegalArgumentException("Wrong parameter " + pointType + " = " + dp.getMean());
        } else if (pointType.equalsIgnoreCase(DataPointType.CLOUDCOVER.name())) {
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setCloudCover(dp);
            }
            else throw new IllegalArgumentException("Wrong parameter " + pointType + " = " + dp.getMean());
        } else if (pointType.equalsIgnoreCase(DataPointType.PRECIPITATION.name())) {
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setPrecipitation(dp);
            }
            else throw new IllegalArgumentException("");
        }
        ai.setLastUpdateTime(System.currentTimeMillis());
    }

    @Override
    public void saveAirport(AirportData ad) {
        LOGGER.fine("Save airport: " + ad);
        if (ad == null || ad.getIata() == null) {
            LOGGER.severe("Cannot save airport");
            return;
        }
        getAirportDataStorage().put(ad.getIata(), ad);
    }

    @Override
    public void deleteAirport(String iataCode) {
        LOGGER.fine("Delete airport: " + iataCode);
        if (iataCode == null) {
            LOGGER.severe("Cannot delete airport");
            return;
        }
        deleteAtmosphericInformation(iataCode);
        getAirportDataStorage().remove(iataCode);
    }

    @Override
    public void deleteAtmosphericInformation(String iataCode) {
        LOGGER.fine("Delete atmospheric information: " + iataCode);
        if (iataCode == null)  {
            LOGGER.severe("Cannot delete atmospheric information");
            return;
        }
        getAtmosphericInformationDataStorage().remove(iataCode);
    }

}