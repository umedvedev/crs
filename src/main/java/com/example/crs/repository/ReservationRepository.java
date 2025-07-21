package com.example.crs.repository;

import com.example.crs.entity.CarType;
import com.example.crs.entity.Reservation;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


@Repository
public class ReservationRepository {

    private final List<Reservation> reservations = new ArrayList<>();

    public List<Reservation> getReservationsByType(CarType type) {
        return reservations.stream()
                .filter(reservation -> reservation.type().equals(type))
                .toList();
    }

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }
}
