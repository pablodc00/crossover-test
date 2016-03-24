package com.crossover.trial.weather;

import com.crossover.trial.weather.impl.Storage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Airport endpoint tests
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/simpleContext.xml")
public class AirportEndpointTest {

    @Resource
    private WeatherCollectorEndpoint endpoint;

    @Before
    public void setUp() throws Exception {
        Storage.AIRPORT_DATA.clear();
        Storage.ATMOSPHERIC_INFORMATION.clear();
        Storage.REQUEST_FREQUENCY.clear();
        Storage.RADIUS_FREQUENCY.clear();

        endpoint.addAirport("BOS", "42.364347", "-71.005181");
        endpoint.addAirport("EWR", "40.6925", "-74.168667");
        endpoint.addAirport("JFK", "40.639751", "-73.778925");
        endpoint.addAirport("LGA", "40.777245", "-73.872608");
        endpoint.addAirport("MMU", "40.79935", "-74.4148747");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddAirport() throws Exception {
        endpoint.addAirport("AAA", "0", "0");
        AirportData ad = (AirportData) endpoint.getAirport("AAA").getEntity();
        assertNotNull(ad);
        assertEquals(ad.getIata(), "AAA");
        Set<String> airports = (Set<String>) endpoint.getAirports().getEntity();
        assertEquals(airports.size(), 6);

        endpoint.addAirport(null, null, null);
        airports = (Set<String>) endpoint.getAirports().getEntity();
        assertEquals(airports.size(), 6);
        endpoint.addAirport("BBB", "-1000", "100");
        airports = (Set<String>) endpoint.getAirports().getEntity();
        assertEquals(airports.size(), 6);
        endpoint.addAirport("BBB", "-90", "190");
        airports = (Set<String>) endpoint.getAirports().getEntity();
        assertEquals(airports.size(), 6);
    }

    @Test
    public void testGetAirport() throws Exception {
        AirportData ad = (AirportData) endpoint.getAirport("BOS").getEntity();
        assertNotNull(ad);
        endpoint.getAirport(null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetAirports() throws Exception {
        Set<String> airports = (Set<String>) endpoint.getAirports().getEntity();
        assertEquals(airports.size(), 5);
    }

    @Test
    public void testDeleteAirport() throws Exception {
        AirportData ad = (AirportData) endpoint.getAirport("BOS").getEntity();
        assertNotNull(ad);
        endpoint.deleteAirport("BOS");
        ad = (AirportData) endpoint.getAirport("BOS").getEntity();
        assertNull(ad);
        endpoint.deleteAirport(null);
    }

}
