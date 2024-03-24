package org.example.fujitsu.service;

import org.example.fujitsu.entity.WeatherData;
import org.example.fujitsu.exceptions.WeatherDataNotFoundException;
import org.example.fujitsu.repository.WeatherDataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DeliveryFeeServiceTest {
    @Mock
    private WeatherDataRepository weatherDataRepository;

    @InjectMocks
    private DeliveryFeeService deliveryFeeService;

    @Test
    public void calculateFee_TartuBike_LowTemperaturRain_ReturnsExtraFee() {
        WeatherData weatherData = new WeatherData(null, "Tartu-Tõravere", "26042", -15.0, 5.0, "Light rain", LocalDateTime.now());
        given(weatherDataRepository.findWeatherDataByStationNameOrdered("Tartu-Tõravere")).willReturn(List.of(weatherData));
        String city = "Tartu-Tõravere";
        String vehicleType = "Bike";
        double baseFee = 2.5;
        double expectedExtraFee = 1.5;
        double expectedTotalFee = baseFee + expectedExtraFee;

        double calculatedFee = deliveryFeeService.calculateDeliveryFee(city, vehicleType);

        assertEquals(expectedTotalFee, calculatedFee);
    }

    @Test
    public void calculateFee_TartuCar_NormalTemperature_ReturnsExtraFee() {
        WeatherData weatherData = new WeatherData(null, "Tartu-Tõravere", "26042", 5.0, 5.0, "Clear", LocalDateTime.now());
        given(weatherDataRepository.findWeatherDataByStationNameOrdered("Tartu-Tõravere")).willReturn(List.of(weatherData));
        String city = "Tartu-Tõravere";
        String vehicleType = "Car";
        double baseFee = 3.5;
        double expectedExtraFee = 0;
        double expectedTotalFee = baseFee + expectedExtraFee;

        double calculatedFee = deliveryFeeService.calculateDeliveryFee(city, vehicleType);

        assertEquals(expectedTotalFee, calculatedFee);
    }

    @Test
    public void calculateFee_TallinnBike_HighWindSnow_ReturnsExtraFee() {
        WeatherData weatherData = new WeatherData(null, "Tallinn-Harku", "26042", 5.0, 15.0, "Snow", LocalDateTime.now());
        given(weatherDataRepository.findWeatherDataByStationNameOrdered("Tallinn-Harku")).willReturn(List.of(weatherData));
        String city = "Tallinn-Harku";
        String vehicleType = "Bike";
        double baseFee = 3;
        double expectedExtraFee = 1.5;
        double expectedTotalFee = baseFee + expectedExtraFee;

        double calculatedFee = deliveryFeeService.calculateDeliveryFee(city, vehicleType);

        assertEquals(expectedTotalFee, calculatedFee);
    }

    @Test
    public void calculateFee_BikeWithThunderWeather_ThrowsException() {

        WeatherData weatherData = new WeatherData(null, "Pärnu", "26050", 15.0, 1.5, "Thunder", LocalDateTime.now());
        given(weatherDataRepository.findWeatherDataByStationNameOrdered("Pärnu")).willReturn(List.of(weatherData));

        String city = "Pärnu";
        String vehicleType = "Scooter";


        assertThrows(UnsupportedOperationException.class, () -> deliveryFeeService.calculateDeliveryFee(city, vehicleType),
                "Usage of selected vehicle type is forbidden due to dangerous weather conditions");
    }

    @Test
    public void calculateFee_BikeWithTooHighWind_ThrowsException() {

        WeatherData weatherData = new WeatherData(null, "Pärnu", "26050", 15.0, 25, "Clear", LocalDateTime.now());
        given(weatherDataRepository.findWeatherDataByStationNameOrdered("Pärnu")).willReturn(List.of(weatherData));

        String city = "Pärnu";
        String vehicleType = "Bike";


        assertThrows(UnsupportedOperationException.class, () -> deliveryFeeService.calculateDeliveryFee(city, vehicleType),
                "Usage of selected vehicle type is forbidden due to dangerous weather conditions");
    }

    @Test
    void calculateDeliveryFee_withNoData_throwsException() {
        when(weatherDataRepository.findWeatherDataByStationNameOrdered("Nonexistent City"))
                .thenReturn(Collections.emptyList());

        assertThrows(WeatherDataNotFoundException.class,
                () -> deliveryFeeService.calculateDeliveryFee("Nonexistent City", "Bike"),
                "No weather data is available for the city.");
    }

}
