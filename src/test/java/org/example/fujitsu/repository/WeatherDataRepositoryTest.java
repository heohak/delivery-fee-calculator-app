package org.example.fujitsu.repository;

import org.example.fujitsu.entity.WeatherData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class WeatherDataRepositoryTest {

    @Autowired
    private WeatherDataRepository weatherDataRepository;

    @BeforeEach
    void setUp() {
        WeatherData weatherData1 = new WeatherData(null, "Tallinn-Harku", "26038", 5.0, 3.5, "Light rain", LocalDateTime.now().minusDays(1));
        WeatherData weatherData2 = new WeatherData(null, "Tartu-Tõravere", "26042", 2.0, 5.0, "Snow", LocalDateTime.now().minusHours(2));
        WeatherData weatherData3 = new WeatherData(null, "Tallinn-Harku", "26038", 10.0, 2.0, "Sunny", LocalDateTime.now());
        WeatherData weatherData4 = new WeatherData(null, "Pärnu", "26050", 15.0, 1.5, "Cloudy", LocalDateTime.now().minusHours(1));
        WeatherData weatherData5 = new WeatherData(null, "Tallinn-Harku", "26038", 0.0, 4.5, "Heavy rain", LocalDateTime.now().minusDays(2));

        weatherDataRepository.saveAll(List.of(weatherData1, weatherData2, weatherData3, weatherData4, weatherData5));
    }

    @Test
    void whenFindWeatherDataByStationNameOrdered_thenReturnWeatherDataListOrdered() {
        List<WeatherData> result = weatherDataRepository.findWeatherDataByStationNameOrdered("Tallinn-Harku");

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getWeatherPhenomenon()).isEqualTo("Sunny");
        assertThat(result.get(1).getWeatherPhenomenon()).isEqualTo("Light rain");
        assertThat(result.get(2).getWeatherPhenomenon()).isEqualTo("Heavy rain");
    }
}
