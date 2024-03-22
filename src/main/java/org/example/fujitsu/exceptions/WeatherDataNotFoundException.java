package org.example.fujitsu.exceptions;

public class WeatherDataNotFoundException extends RuntimeException{
    public WeatherDataNotFoundException(String city) {
        super("No weather data available for " + city);
    }
}
