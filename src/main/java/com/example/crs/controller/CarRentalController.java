package com.example.crs.controller;

import com.example.crs.entity.CarType;
import com.example.crs.entity.Reservation;
import com.example.crs.service.CarRentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
class CarRentalController {

    private final CarRentalService carRentalService;

    public CarRentalController(@Autowired CarRentalService carRentalService) {
        this.carRentalService = carRentalService;
    }

    @PutMapping("/rent")
    public ResponseEntity<Reservation> rentCar(
            @RequestParam("type") CarType type,
            @RequestParam("dateTime") LocalDateTime dateTime,
            @RequestParam("days") int days
    ) {
        return ResponseEntity.ok(carRentalService.makeReservation(type, dateTime, days));
    }
}