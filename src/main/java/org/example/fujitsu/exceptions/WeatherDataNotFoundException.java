package org.example.fujitsu.exceptions;

public class WeatherDataNotFoundException extends RuntimeException{
    /**
     * This exception is typically thrown when an attempt is made to calculate
     * the delivery fee or retrieve weather data for a city, but no corresponding
     * weather data is found in the system.
     *
     * @param city The name of the city for which weather data was not found.
     */
    public WeatherDataNotFoundException(String city) {
        super("No weather data available for " + city);
    }
}
