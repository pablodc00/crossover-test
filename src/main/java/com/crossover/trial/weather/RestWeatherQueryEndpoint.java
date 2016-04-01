package com.crossover.trial.weather;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Weather App REST endpoint allows clients to query atmospheric information and check health stats.
 * Currently, all data is held in memory. The end point deploys to a single container.
 */
@Component
public class RestWeatherQueryEndpoint implements WeatherQueryEndpoint {

    private final static Logger LOGGER = Logger.getLogger(RestWeatherQueryEndpoint.class.getName());

    @Resource
    private AirportDao airportDao;
    @Resource
    private PerformanceDao performanceDao;

    @Override
    public String ping() {
        RequestFrequencyData data = performanceDao.getPerformanceData();

        Map<String, Object> result = new HashMap<>();
        result.put("datasize", data.getDataSize());
        result.put("iata_freq", data.getAirportFrequency());
        result.put("radius_freq", data.getRadiusFrequency());

        try {
            return new ObjectMapper().writeValueAsString(result);
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, "Cannot serialize performance data", e);
            return "";
        }
    }

    @Override
    public Response weather(String iata, String radiusString) {
        double radius = radiusString == null || radiusString.trim().isEmpty() ? 0 : Double.valueOf(radiusString);
        if (radius < 0) radius = 0;
        performanceDao.updateRequestFrequency(iata, radius);

        List<AtmosphericInformation> result = new ArrayList<>();
        if (iata == null) return Response.status(Response.Status.BAD_REQUEST).entity(result).build();

        if (radius == 0) {
            AtmosphericInformation ai = airportDao.findAtmosphericInformation(iata);
            if (ai != null) {
                result.add(ai);
            }
        } else {
            result.addAll(airportDao.findAtmosphericInformationNearbyAirport(iata, radius));
        }

        return Response.status(Response.Status.OK).entity(result).build();
    }

}
