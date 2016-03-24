package com.crossover.trial.weather;

import com.crossover.trial.weather.impl.Storage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

/**
 * Weather endpoint tests
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")
public class WeatherEndpointTest {

    @Resource
    private WeatherQueryEndpoint _query;
    @Resource
    private WeatherCollectorEndpoint _update;

    @Before
    public void setUp() throws Exception {
        Storage.AIRPORT_DATA.clear();
        Storage.ATMOSPHERIC_INFORMATION.clear();
        Storage.REQUEST_FREQUENCY.clear();
        Storage.RADIUS_FREQUENCY.clear();

        _update.addAirport("BOS", "42.364347", "-71.005181");
        _update.addAirport("EWR", "40.6925", "-74.168667");
        _update.addAirport("JFK", "40.639751", "-73.778925");
        _update.addAirport("LGA", "40.777245", "-73.872608");
        _update.addAirport("MMU", "40.79935", "-74.4148747");

        DataPoint dp = new DataPoint(10, 10, 20, 30, 22);
        _update.updateWeather("BOS", "wind", new ObjectMapper().writeValueAsString(dp));
        _query.weather("BOS", "0").getEntity();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGet() throws Exception {
        List<AtmosphericInformation> ais = (List<AtmosphericInformation>) _query.weather("BOS", "0").getEntity();
        DataPoint dp = new DataPoint(10, 10, 20, 30, 22);
        assertEquals(ais.get(0).getWind(), dp);
        Response response = _query.weather(null, "0");
        assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
        response = _query.weather("AAA", null);
        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetNearby() throws Exception {
        // check datasize response
        ObjectMapper mapper = new ObjectMapper();
        DataPoint dp = new DataPoint(10, 10, 20, 30, 22);
        _update.updateWeather("JFK", "wind", mapper.writeValueAsString(dp));
        dp.setMean(40);
        _update.updateWeather("EWR", "wind", mapper.writeValueAsString(dp));
        dp.setMean(30);
        _update.updateWeather("LGA", "wind", mapper.writeValueAsString(dp));

        List<AtmosphericInformation> ais = (List<AtmosphericInformation>) _query.weather("JFK", "200").getEntity();
        assertEquals(3, ais.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUpdate() throws Exception {
        DataPoint dp = new DataPoint(10, 10, 20, 30, 22);
        _update.updateWeather("BOS", "wind", new ObjectMapper().writeValueAsString(dp));
        _query.weather("BOS", "0").getEntity();

        RequestFrequencyData rfd = new ObjectMapper().readValue(_query.ping(), RequestFrequencyData.class);
        assertEquals(1, rfd.getDataSize());

        DataPoint cloudCoverDp = new DataPoint(4, 10, 60, 100, 50);
        _update.updateWeather("BOS", "cloudcover", new ObjectMapper().writeValueAsString(cloudCoverDp));

        List<AtmosphericInformation> ais = (List<AtmosphericInformation>) _query.weather("BOS", "0").getEntity();
        assertEquals(ais.get(0).getWind(), dp);
        assertEquals(ais.get(0).getCloudCover(), cloudCoverDp);
    }

    @Test
    public void testPing() throws Exception {
        _query.weather("JFK", String.valueOf(5));
        _query.weather("JFK", String.valueOf(5));

        RequestFrequencyData rfd = new ObjectMapper().readValue(_query.ping(), RequestFrequencyData.class);
        assertNotNull(rfd.getDataSize());
        assertNotNull(rfd.getAirportFrequency());
        assertNotNull(rfd.getRadiusFrequency());
    }

}
