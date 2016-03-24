package com.crossover.trial.weather;

import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import org.glassfish.grizzly.PortRange;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.servlet.WebappContext;

import javax.servlet.ServletRegistration;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A main method used to test the Weather Application. You are free to modify this main method
 * as you wish - it's in not used by the grader.
 */
public class WeatherServer {

    private static HttpServer server;
    private int port = -1;

    private boolean production = false;

    public WeatherServer() {
    }

    public WeatherServer(boolean production) {
        this.production = production;
    }

    public static void main(String[] args) throws InterruptedException {
        WeatherServer server;
        String mode = System.getProperty("mode");
        if(mode != null && mode.equalsIgnoreCase("production")) {
            System.out.println("Starting Weather Server in production mode");
            server = new WeatherServer(true);
        }
        else {
            server = new WeatherServer();
        }
        server.start();
        Thread.currentThread().join();
        server.stop();
    }

    public void start() {
        try {
            System.out.println("Starting Weather Server");

            server = new HttpServer();
            NetworkListener listener;
            if(port == -1) listener = new NetworkListener("grizzly2", "localhost", new PortRange(9000, 9999));
            else listener = new NetworkListener("grizzly2", "localhost", port);
            server.addListener(listener);

            WebappContext ctx = new WebappContext("ctx", "/");
            final ServletRegistration reg = ctx.addServlet("spring", new SpringServlet());
            reg.addMapping("/*");
            ctx.addContextInitParameter("contextConfigLocation", production ? "classpath:/hazelcastContext.xml" : "classpath:/simpleContext.xml");
            ctx.addListener("org.springframework.web.context.ContextLoaderListener");
            ctx.addListener("org.springframework.web.context.request.RequestContextListener");
            ctx.deploy(server);

            server.start();
            port = listener.getPort();
            System.out.println("Weather Server started on http://localhost:" + port);
        } catch (IOException ex) {
            Logger.getLogger(WeatherServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void stop() {
        server.shutdown();
    }

    public int getPort() {
        return port;
    }
}
