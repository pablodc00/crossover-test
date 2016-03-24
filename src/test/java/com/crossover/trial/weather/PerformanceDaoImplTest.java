package com.crossover.trial.weather;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.assertTrue;

/**
 * Performance service tests
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")
public class PerformanceDaoImplTest {

    @Resource
    private PerformanceDao dao;
    @Resource
    private AirportDao airportDao;

    @Test
    public void testUpdateRequestFrequency() throws Exception {
        dao.updateRequestFrequency(null, -1000d);
        dao.updateRequestFrequency("AAA", 0d);

        RequestFrequencyData rfd = dao.getPerformanceData();
        assertTrue(rfd.getDataSize() == 0);

        AirportData ad = new AirportData("AAA", 1.0, 2.0);
        airportDao.saveAirport(ad);
        airportDao.updateAtmosphericInformation("AAA", "wind", new DataPoint(10, 20, 30, 40, 50));

        dao.updateRequestFrequency("AAA", 0d);
        dao.updateRequestFrequency("AAA", 5d);
        dao.updateRequestFrequency("AAA", 5d);

        rfd = dao.getPerformanceData();
        assertTrue(rfd.getDataSize() == 1);
        assertTrue(rfd.getAirportFrequency().size() == 1);
        assertTrue(rfd.getRadiusFrequency().length == 6);
        assertTrue(rfd.getRadiusFrequency()[0] == 1);
        assertTrue(rfd.getRadiusFrequency()[5] == 2);
    }

    @Test
    public void testGetPerformanceData() throws Exception {
        AirportData ad = new AirportData("AAA", 1.0, 2.0);
        airportDao.saveAirport(ad);
        airportDao.updateAtmosphericInformation("AAA", "wind", new DataPoint(10, 20, 30, 40, 50));

        ad = new AirportData("BBB", 1.0, 3.0);
        airportDao.saveAirport(ad);
        airportDao.updateAtmosphericInformation("BBB", "wind", new DataPoint(10, 20, 30, 40, 50));

        dao.updateRequestFrequency("AAA", -1000d);
        dao.updateRequestFrequency("AAA", 0d);
        dao.updateRequestFrequency("AAA", 5d);
        dao.updateRequestFrequency("AAA", 5d);
        dao.updateRequestFrequency("AAA", 1000000d);
        dao.updateRequestFrequency("AAA", 2000000d);
        dao.updateRequestFrequency("AAA", 3000000d);

        RequestFrequencyData rfd = dao.getPerformanceData();
        assertTrue(rfd.getDataSize() == 2);
        assertTrue(rfd.getAirportFrequency().get("AAA") == 1.0);
        assertTrue(rfd.getRadiusFrequency().length == 1001);
        assertTrue(rfd.getRadiusFrequency()[0] == 2);
        assertTrue(rfd.getRadiusFrequency()[1] == 0);
        assertTrue(rfd.getRadiusFrequency()[5] == 2);
        assertTrue(rfd.getRadiusFrequency()[1000] == 3);

    }

    @Test
    public void testGetPerformanceDataAirportFrequency() throws Exception {
        AirportData ad = new AirportData("AAA", 1.0, 2.0);
        airportDao.saveAirport(ad);
        airportDao.updateAtmosphericInformation("AAA", "wind", new DataPoint(10, 20, 30, 40, 50));

        ad = new AirportData("BBB", 1.0, 3.0);
        airportDao.saveAirport(ad);
        airportDao.updateAtmosphericInformation("BBB", "wind", new DataPoint(10, 20, 30, 40, 50));

        dao.updateRequestFrequency("AAA", 0d);
        dao.updateRequestFrequency("BBB", -0d);

        RequestFrequencyData rfd = dao.getPerformanceData();
        assertTrue(rfd.getDataSize() == 2);
        assertTrue(rfd.getAirportFrequency().get("AAA") == 0.5);
        assertTrue(rfd.getAirportFrequency().get("BBB") == 0.5);
    }

    @After
    public void tearDown() {
        airportDao.deleteAirport("AAA");
        airportDao.deleteAirport("BBB");
        dao.clear();
    }

}
