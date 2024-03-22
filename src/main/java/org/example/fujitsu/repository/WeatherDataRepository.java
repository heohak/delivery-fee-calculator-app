package org.example.fujitsu.repository;

import org.example.fujitsu.entity.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {
    @Query("SELECT wd FROM WeatherData wd WHERE wd.stationName = :stationName ORDER BY wd.observationTimestamp DESC")
    List<WeatherData> findWeatherDataByStationNameOrdered(@Param("stationName") String stationName);
}
