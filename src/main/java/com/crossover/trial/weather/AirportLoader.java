package com.crossover.trial.weather;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.*;

/**
 * A simple airport loader which reads a file from disk and sends entries to the webservice
 */
public class AirportLoader {

    /**
     * end point to supply updates
     */
    private WebTarget collect;

    public AirportLoader(String weatherServerURL) {
        Client client = ClientBuilder.newClient();
        collect = client.target(weatherServerURL + "/collect");
    }

    public static void main(String args[]) throws IOException {
        if (args.length != 2) {
            System.out.println("USAGE: java com.crossover.trial.weather.AirportLoader file_name weather_server_url");
            return;
        }

        File airportDataFile = new File(args[0]);
        if (!airportDataFile.exists() || airportDataFile.length() == 0) {
            System.err.println(airportDataFile + " is not a valid input");
            System.exit(1);
        }

        AirportLoader al = new AirportLoader(args[1]);
        al.upload(new FileInputStream(airportDataFile));
        System.exit(0);
    }

    public void upload(InputStream airportDataStream) throws IOException {
        Response response = collect.path("/ping").request().get();
        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            System.out.println("Service AWS at " + collect.getUri() + "is unavailable");
            return;
        }

        System.out.printf("%1$-8s %2$-20s %3$-20s %4$-8s \n", "IATA", "Latitude", "Longitude", "Result");
        System.out.println("==========================================================");

        for (CSVRecord record : CSVFormat.DEFAULT.parse(new BufferedReader(new InputStreamReader(airportDataStream)))) {
            String iataCode = record.get(4);
            String latitude = record.get(6);
            String longitude = record.get(7);
            response = collect.path("/airport/" + iataCode + "/" + latitude + "/" + longitude).
                    request().post(Entity.entity("", "application/json"));

            System.out.printf("%1$-8s %2$-20s %3$-20s %4$-8s \n", iataCode, latitude, longitude,
                    (response.getStatus() == Response.Status.OK.getStatusCode()) ? "OK" : "ERROR");
        }
    }

}
