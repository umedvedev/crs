package com.example.crs.service;

import com.example.crs.entity.CarType;
import com.example.crs.entity.Reservation;
import com.example.crs.exception.RentException;
import com.example.crs.repository.CarRepository;
import com.example.crs.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

@Service
public class CarRentalService {

    private final CarRepository carRepository;

    private final ReservationRepository reservationRepository;

    public CarRentalService(
            @Autowired CarRepository carRepository,
            @Autowired ReservationRepository reservationRepository) {
        this.carRepository = carRepository;
        this.reservationRepository = reservationRepository;
    }

    public Reservation makeReservation(CarType type, LocalDateTime startDateTime, int days) {
        validateInput(type, startDateTime, days);

        LocalDateTime endDateTime = startDateTime.plusDays(days);

        Integer carCapacityByType = carRepository.getCarInventory().get(type);

        List<Reservation> reservationsByType = reservationRepository.getReservationsByType(type).
                stream()
                .filter(isIntersected(startDateTime, endDateTime))
                .toList();

        if (carCapacityByType > reservationsByType.size()) {
            Reservation reservation = new Reservation(type, startDateTime, endDateTime);
            reservationRepository.addReservation(reservation);
            return reservation;
        } else {
            throw new RentException("No available cars of type " + type + " for the requested period");
        }
    }

    private static void validateInput(CarType type, LocalDateTime startDateTime, int days) {
        if (type == null) {
            throw new RentException("Car type must not be null");
        }
        if (startDateTime == null) {
            throw new RentException("Start date and time must not be null");
        }
        if (days <= 0) {
            throw new RentException("Days must be greater than zero");
        }
        if (startDateTime.isBefore(LocalDateTime.now())) {
            throw new RentException("Start date and time must not be in the past");
        }
    }

    private static Predicate<Reservation> isIntersected(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return r -> {
            LocalDateTime rSd = r.startDate();
            LocalDateTime rEd = r.endDate();
            return !(startDateTime.isAfter(rEd) || endDateTime.isBefore(rSd));
        };
    }
}
