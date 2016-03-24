package com.crossover.trial.weather;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Airport loader tests
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")
public class AirportLoaderTest {

    private static WeatherServer server;
    private static PrintStream systemOut = System.out;
    private static ByteArrayOutputStream out = new ByteArrayOutputStream(1000000);
    private static String WEATHER_SERVER_URL = "http://localhost:";

    @Resource
    private AirportDao airportDao;

    private WebTarget collect;

    @BeforeClass
    public static void init() throws Exception {
        server = new WeatherServer();
        server.start();
        WEATHER_SERVER_URL = WEATHER_SERVER_URL + server.getPort();
        System.setOut(new PrintStream(out));
    }

    @Before
    public void setUp() throws Exception {
        Client client = ClientBuilder.newClient();
        collect = client.target(WEATHER_SERVER_URL + "/collect");
    }

    @Test
    public void test() throws Exception {
        new AirportLoader(WEATHER_SERVER_URL).upload(AirportLoader.class.getResourceAsStream("/airports.dat"));
        String output = new String(out.toByteArray(), 0, out.size());

        assertTrue(output.length() > 0);
        assertTrue(output.contains("JFK"));
        assertTrue(output.contains("40.639751"));
        assertTrue(output.contains("-73.778925"));

        WebTarget path = collect.path("/airport/BOS");
        Response response = path.request().get();
        String a = response.readEntity(String.class);

        assertNotEquals(a.indexOf("BOS"), -1);
    }

    @After
    public void tearDown() throws Exception {
        airportDao.getAllAirportCodes().forEach(airportDao::deleteAirport);
    }

    @AfterClass
    public static void destroy() throws Exception {
        System.setOut(systemOut);
        server.stop();
    }

}
