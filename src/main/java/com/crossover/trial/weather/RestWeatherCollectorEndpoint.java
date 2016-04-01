package com.crossover.trial.weather;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A REST implementation of the WeatherCollector API. Accessible only to airport weather collection
 * sites via secure VPN.
 */
@Component
public class RestWeatherCollectorEndpoint implements WeatherCollectorEndpoint {

    private final static Logger LOGGER = Logger.getLogger(RestWeatherCollectorEndpoint.class.getName());

    @Resource
    private AirportDao airportDao;

    @Override
    public Response ping() {
        return Response.status(Response.Status.OK).entity("ready").build();
    }

    @Override
    public Response updateWeather(String iataCode,
                                  String pointType,
                                  String datapointJson) {

        DataPoint dp;
        try {
            dp = new ObjectMapper().readValue(datapointJson, DataPoint.class);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Cannot read datapoint", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (iataCode == null || pointType == null || dp == null || airportDao.findAirportData(iataCode) == null) {
            LOGGER.log(Level.SEVERE, "Bad parameters: iataCode = " + iataCode + ", pointType = " + pointType + ", dp = " + dp);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        try {
            airportDao.updateAtmosphericInformation(iataCode, pointType, dp);
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.status(Response.Status.OK).build();
    }

    @Override
    public Response getAirports() {
        Set<String> result = new HashSet<>();
        result.addAll(airportDao.getAllAirportCodes());

        return Response.status(Response.Status.OK).entity(result).build();
    }

    @Override
    public Response getAirport(String iata) {
        AirportData ad = airportDao.findAirportData(iata);
        return Response.status(Response.Status.OK).entity(ad).build();
    }

    @Override
    public Response addAirport(String iata,
                               String latString,
                               String longString) {

        if (iata == null || iata.length() != 3 || latString == null || longString == null) {
            LOGGER.log(Level.SEVERE, "Bad parameters: iata = " + iata + ", latString = " + latString + ", longString = " + longString);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Double latitude;
        Double longitude;
        try {
            latitude = Double.valueOf(latString);
            longitude = Double.valueOf(longString);
        } catch (NumberFormatException ex) {
            LOGGER.severe("Wrong airport coordinates latString = " + latString + ", longString = " + longString);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            LOGGER.severe("Wrong airport coordinates latString = " + latString + ", longString = " + longString);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        AirportData ad = new AirportData();
        ad.setIata(iata);
        ad.setLatitude(latitude);
        ad.setLongitude(longitude);

        airportDao.saveAirport(ad);

        return Response.status(Response.Status.OK).build();
    }

    @Override
    public Response deleteAirport(String iata) {
        airportDao.deleteAirport(iata);
        return Response.status(Response.Status.OK).build();
    }

    @Override
    public Response exit() {
        return Response.status(Response.Status.FORBIDDEN).build();
    }

}
