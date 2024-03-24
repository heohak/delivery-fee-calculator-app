package org.example.fujitsu.service;

import lombok.AllArgsConstructor;
import org.example.fujitsu.entity.WeatherData;
import org.example.fujitsu.exceptions.WeatherDataNotFoundException;
import org.example.fujitsu.repository.WeatherDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
@AllArgsConstructor
@Service
public class DeliveryFeeService {

    private static final Logger log = LoggerFactory.getLogger(DeliveryFeeService.class);

    private WeatherDataRepository weatherDataRepository;

    /**
     * Calculates the delivery fee based on the city, vehicle type, and current weather conditions.
     * The method first retrieves the latest weather data for the given city. It then calculates the base fee
     * according to the vehicle type and applies any extra fees that may be warranted by the current weather conditions.
     * Extra fees are determined based on air temperature, wind speed, and weather phenomena such as rain, snow, or sleet.
     *
     * @param city The city for which the delivery fee is being calculated.
     * @param vehicleType The type of vehicle used for delivery, affecting the base delivery fee.
     * @return The total delivery fee, which includes the base fee and any extra fees due to weather conditions.
     * @throws WeatherDataNotFoundException if no weather data is available for the specified city.
     * @throws UnsupportedOperationException if the weather conditions make the usage of the selected vehicle type forbidden.
     */
    public double calculateDeliveryFee(String city, String vehicleType) {
        List<WeatherData> weatherDataList = weatherDataRepository.findWeatherDataByStationNameOrdered(city);
        if (weatherDataList.isEmpty()) {
            log.warn("No weather data available for {}", city);
            throw new WeatherDataNotFoundException(city);
        }
        WeatherData latestWeather = weatherDataList.get(0);

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
            } else if (weatherPhenomenon.contains("rain") || weatherPhenomenon.contains("shower")) {
                extraFee += 0.5;
            }

            if (weatherPhenomenon.contains("glaze") || weatherPhenomenon.contains("hail") || weatherPhenomenon.contains("thunder")) {
                throw new UnsupportedOperationException("Usage of selected vehicle type is forbidden due to dangerous weather conditions");
            }
        }

        if ("Bike".equals(vehicleType)) {
            if (weatherData.getWindSpeed() > 20) {
                throw new UnsupportedOperationException("Usage of selected vehicle type is forbidden due to high wind speed");
            } else if (weatherData.getWindSpeed() >= 10) {
                extraFee += 0.5;
            }
        }

        return extraFee;
    }
}
