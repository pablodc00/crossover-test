package com.crossover.trial.weather;

import org.glassfish.grizzly.PortRange;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.servlet.ServletRegistration;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * It's a production level server!
 * <p>
 * Try to start several nodes using: java -jar [-Dmode=production] [-Dport=XXXX] weather-1.1.0.jar
 *
 * @see com.crossover.trial.weather.WeatherClusterIntegrationTest
 */
public class WeatherServer {

    private static HttpServer server;

    private int port = -1;
    private boolean production = false;

    public WeatherServer() {}

    public static void main(String[] args) throws InterruptedException {
        WeatherServer server = new WeatherServer();

        String mode = System.getProperty("mode");
        server.production = (mode != null && mode.equalsIgnoreCase("production"));

        String port = System.getProperty("port");
        if(port != null) {
            try {
                server.port = Integer.valueOf(port);
            }
            catch(NumberFormatException ignore) {}
        }

        server.start();
        Thread.currentThread().join();
        server.stop();
    }

    public void start() {
        try {
            System.out.println("Starting Weather Server" + (production ? " in production mode" : " in test mode"));

            server = new HttpServer();
            NetworkListener listener;
            if (port == -1) listener = new NetworkListener("grizzly2", "localhost", new PortRange(9000, 9999));
            else listener = new NetworkListener("grizzly2", "localhost", port);
            server.addListener(listener);

            WebappContext ctx = new WebappContext("ctx", "/");
            ctx.addContextInitParameter("contextConfigLocation", production ? "classpath:/hazelcastContext.xml" : "classpath:/simpleContext.xml");
            ctx.addListener("org.springframework.web.context.ContextLoaderListener");
            ctx.addListener("org.springframework.web.context.request.RequestContextListener");

            final ServletRegistration reg = ctx.addServlet("spring", new ServletContainer());
            reg.addMapping("/*");
            reg.setInitParameter("javax.ws.rs.Application", "com.crossover.trial.weather.WeatherApplication");

            ctx.deploy(server);

            server.start();
            port = listener.getPort();
            System.out.println("Weather Server started on http://localhost:" + port);
        } catch (IOException ex) {
            Logger.getLogger(WeatherServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void stop() {
        server.shutdownNow();
    }

    public int getPort() {
        return port;
    }
}
