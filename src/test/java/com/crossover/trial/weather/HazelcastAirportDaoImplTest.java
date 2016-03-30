package com.crossover.trial.weather;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests for an implementation of DAO using Hazelcast framework
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/hazelcastContext.xml")
public class HazelcastAirportDaoImplTest extends AirportDaoImplTest {

    @Test
    public void testFindAirportData() {
        super.testFindAirportData();
    }

    @Test
    public void testFindAirportDataNull() {
        super.testFindAirportDataNull();
    }

    @Test
    public void testFindAirportDataUnknown() {
        super.testFindAirportDataUnknown();
    }

    @Test
    public void testFindNearbyAirports() {
        super.testFindNearbyAirports();
    }

    @Test
    public void testFindNearbyAirportsNull() {
        super.testFindNearbyAirportsNull();
    }

    @Test
    public void testFindNearbyAirportsUnknown() {
        super.testFindNearbyAirportsUnknown();
    }

    @Test
    public void testGetAllAirportCodes() {
        super.testGetAllAirportCodes();
    }

    @Test
    public void testFindAtmosphericInformation() {
        super.testFindAtmosphericInformation();
    }

    @Test
    public void testFindAtmosphericInformationNull() {
        super.testFindAtmosphericInformationNull();
    }

    @Test
    public void testFindAtmosphericInformationUnknown() {
        super.testFindAtmosphericInformationUnknown();
    }

    @Test
    public void testFindAtmosphericInformationNearbyAirport() {
        super.testFindAtmosphericInformationNearbyAirport();
    }

    @Test
    public void testFindAtmosphericInformationNearbyAirportNull() {
        super.testFindAtmosphericInformationNearbyAirportNull();
    }

    @Test
    public void testFindAtmosphericInformationNearbyAirportUnknown() {
        super.testFindAtmosphericInformationNearbyAirportUnknown();
    }

    @Test
    public void testUpdateAtmosphericInformation() {
        super.testUpdateAtmosphericInformation();
    }

    @Test
    public void testUpdateAtmosphericInformationNull() {
        super.testUpdateAtmosphericInformationNull();
    }

    @Test
    public void testUpdateAtmosphericInformationUnknown() {
        super.testUpdateAtmosphericInformationUnknown();
    }

    @Test
    public void testSaveAirport() {
        super.testSaveAirport();
    }

    @Test
    public void testSaveAirportNull() {
        super.testSaveAirportNull();
    }

    @Test
    public void testDeleteAirport() {
        super.testDeleteAirport();
    }

    @Test
    public void testDeleteAirportNull() {
        super.testDeleteAirportNull();
    }

    @Test
    public void testDeleteAtmosphericInformation() {
        super.testDeleteAtmosphericInformation();
    }

    @Test
    public void testDeleteAtmosphericInformationNull() {
        super.testDeleteAtmosphericInformationNull();
    }

    @Test
    public void testDeleteAtmosphericInformationUnknown() {
        super.testDeleteAtmosphericInformationUnknown();
    }

}
