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

        @GetMapping("/{city}/{vehicleType}")
        public ResponseEntity<?> getDeliveryFee(@PathVariable String city, @PathVariable String vehicleType) {
            try {
                String stationName = mapCityToStationName(city);
                double fee = deliveryFeeService.calculateDeliveryFee(stationName, vehicleType);
                return ResponseEntity.ok(fee);
            } catch (UnsupportedOperationException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            } catch (Exception e) {

                return ResponseEntity.internalServerError().body("An unexpected error occurred");
            }
        }

        private String mapCityToStationName(String city) {
            switch (city.toLowerCase()) {
                case "tartu":
                    return "Tartu-Tõravere";
                case "tallinn":
                    return "Tallinn-Harku";
                case "pärnu":
                    return "Pärnu";
                default:
                    return city;
            }
        }
    }
