package com.crossover.trial.weather;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;

/**
 * Created by ekonovalov on 30.03.2016.
 */
public class WeatherApplication extends ResourceConfig {

    public WeatherApplication() {
        register(RequestContextFilter.class);
        register(RestWeatherCollectorEndpoint.class);
        register(RestWeatherQueryEndpoint.class);
    }

}
