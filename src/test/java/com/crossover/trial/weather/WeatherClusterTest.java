package com.crossover.trial.weather;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.logging.LogManager;

import static org.junit.Assert.*;

/**
 * Created by ekonovalov on 24.03.2016.
 */
public class WeatherClusterTest {

    private static WeatherServer[] servers = new WeatherServer[2];
    private Client client = ClientBuilder.newClient();

    @BeforeClass
    public static void init() throws InterruptedException, IOException {
        for (int i = 0; i < servers.length; i++) {
            WeatherServer server = new WeatherServer(true);
            servers[i] = server;
        }
        for (WeatherServer server : servers) {
            server.start();
        }
    }

    @AfterClass
    public static void destroy() {
        for (WeatherServer server : servers) {
            if (server != null) server.stop();
        }
    }

    @Test
    public void testPing() {
        WebTarget collect;
        for (WeatherServer server : servers) {
            collect = client.target("http://localhost:" + server.getPort() + "/collect");
            WebTarget path = collect.path("/ping");
            Response response = path.request().get();
            assertTrue(response.getStatus() == 200);
        }
    }

    @Test
    public void testCrossServerRequests() {
        //put data to a node
        WebTarget collect = client.target("http://localhost:" + servers[0].getPort() + "/collect");
        Response response = collect.path("/airport/BOS/10/20").request().post(Entity.entity("", "application/json"));
        assertTrue(response.getStatus() == 200);

        //ask another node for the data
        collect = client.target("http://localhost:" + servers[1].getPort() + "/collect");
        AirportData ad = collect.path("/airport/BOS").request().get().readEntity(AirportData.class);
        assertNotNull(ad);
        assertEquals("BOS", ad.getIata());
        assertEquals(10, ad.getLatitude(), 0);
        assertEquals(20, ad.getLongitude(), 0);
    }

    @Test
    public void testKillNode() throws InterruptedException {
        //put data to a node, it'll be replicated to both
        WebTarget collect = client.target("http://localhost:" + servers[0].getPort() + "/collect");
        Response response = collect.path("/airport/BOS/10/20").request().post(Entity.entity("", "application/json"));
        assertTrue(response.getStatus() == 200);

        //stop one node
        servers[1].stop();

        collect = client.target("http://localhost:" + servers[1].getPort() + "/collect");
        WebTarget path = collect.path("/ping");

        //check that node is stopped
        Exception exception = null;
        try {
            response = path.request().get();
        }
        catch(ProcessingException e) {
            exception = e;
        }
        assertNotNull(exception);

        //start the node again
        servers[1] = new WeatherServer(true);
        servers[1].start();

        //check that data is replicated back to the node
        collect = client.target("http://localhost:" + servers[1].getPort() + "/collect");
        AirportData ad = collect.path("/airport/BOS").request().get().readEntity(AirportData.class);
        assertNotNull(ad);

    }

}
