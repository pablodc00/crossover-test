package com.crossover.trial.weather;

/**
 * An internal exception marker
 */
public class WeatherException extends Exception {

    public WeatherException() {
    }

    public WeatherException(String message) {
        super(message);
    }

    public WeatherException(String message, Throwable cause) {
        super(message, cause);
    }

    public WeatherException(Throwable cause) {
        super(cause);
    }

    public WeatherException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
