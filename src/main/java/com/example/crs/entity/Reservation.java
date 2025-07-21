package com.example.crs.entity;

import java.time.LocalDateTime;

public record Reservation(
        CarType type,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}
