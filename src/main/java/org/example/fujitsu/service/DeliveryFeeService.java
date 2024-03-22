package org.example.fujitsu.service;

import org.example.fujitsu.entity.WeatherData;
import org.example.fujitsu.repository.WeatherDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveryFeeService {

    @Autowired
    private WeatherDataRepository weatherDataRepository;

    public double calculateDeliveryFee(String city, String vehicleType) {
        List<WeatherData> weatherDataList = weatherDataRepository.findWeatherDataByStationNameOrdered(city);
        WeatherData latestWeather = weatherDataList.isEmpty() ? null : weatherDataList.get(0);

        if (latestWeather == null) {
            throw new IllegalStateException("No weather data available for " + city);
        }

        double baseFee = calculateBaseFee(city, vehicleType);
        double extraFees = calculateExtraFees(vehicleType, latestWeather);

        return baseFee + extraFees;
    }

    private double calculateBaseFee(String city, String vehicleType) {
        switch (city) {
            case "Tallinn-Harku":
                switch (vehicleType) {
                    case "Car": return 4.0;
                    case "Scooter": return 3.5;
                    case "Bike": return 3.0;
                }
                break;
            case "Tartu-Tõravere":
                switch (vehicleType) {
                    case "Car": return 3.5;
                    case "Scooter": return 3.0;
                    case "Bike": return 2.5;
                }
                break;
            case "Pärnu":
                switch (vehicleType) {
                    case "Car": return 3.0;
                    case "Scooter": return 2.5;
                    case "Bike": return 2.0;
                }
                break;
        }
        return 0;
    }

    private double calculateExtraFees(String vehicleType, WeatherData weatherData) {
        double extraFee = 0.0;
        String weatherPhenomenon = weatherData.getWeatherPhenomenon().toLowerCase();

        if ("Scooter".equals(vehicleType) || "Bike".equals(vehicleType)) {
            if (weatherData.getAirTemperature() < -10) {
                extraFee += 1.0;
            } else if (weatherData.getAirTemperature() >= -10 && weatherData.getAirTemperature() <= 0) {
                extraFee += 0.5;
            }

            if (weatherPhenomenon.contains("snow") || weatherPhenomenon.contains("sleet")) {
                extraFee += 1.0;
            } else if (weatherPhenomenon.contains("rain")) {
                extraFee += 0.5;
            }

            if (weatherPhenomenon.contains("glaze") || weatherPhenomenon.contains("hail") || weatherPhenomenon.contains("thunder")) {
                throw new UnsupportedOperationException("Usage of selected vehicle type is forbidden due to dangerous weather conditions");
            }
        }

        if ("Bike".equals(vehicleType)) {
            if (weatherData.getWindSpeed() > 20) {
                throw new UnsupportedOperationException("Usage of selected vehicle type is forbidden due to high wind speed");
            } else if (weatherData.getWindSpeed() >= 10 && weatherData.getWindSpeed() <= 20) {
                extraFee += 0.5;
            }
        }

        return extraFee;
    }
}
