package org.example.fujitsu.controller;

import org.example.fujitsu.service.DeliveryFeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

    @RestController
    @RequestMapping("/api/delivery-fee")
    public class DeliveryFeeController {

        @Autowired
        private DeliveryFeeService deliveryFeeService;

        /**
         * Endpoint for calculating and retrieving the delivery fee for a specific city and vehicle type.
         * The city name is mapped to a station name, and the fee calculation is based on the latest
         * weather data for that station. This method handles any exceptions by returning an appropriate
         * HTTP response.
         *
         * @param city the city for which the delivery fee is being calculated.
         * @param vehicleType the type of vehicle used for the delivery.
         * @return a ResponseEntity containing either the calculated delivery fee or an error message.
         */
        @GetMapping("/{city}/{vehicleType}")
        public ResponseEntity<?> getDeliveryFee(@PathVariable String city, @PathVariable String vehicleType) {
            try {
                String stationName = mapCityToStationName(city);
                double fee = deliveryFeeService.calculateDeliveryFee(stationName, vehicleType);
                return ResponseEntity.ok(fee);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body("An unexpected error occurred");
            }
        }

        private String mapCityToStationName(String city) {
            return switch (city.toLowerCase()) {
                case "tartu" -> "Tartu-Tõravere";
                case "tallinn" -> "Tallinn-Harku";
                case "pärnu" -> "Pärnu";
                default -> city;
            };
        }
    }
