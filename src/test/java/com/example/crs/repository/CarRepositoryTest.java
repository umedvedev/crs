package com.example.crs.repository;

import com.example.crs.entity.CarType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CarRepositoryTest {

    private final CarRepository testee = new CarRepository();

    @Test
    void getCarInventory() {
        assertThat(testee.getCarInventory())
                .isNotNull()
                .containsEntry(CarType.VAN, 1)
                .containsEntry(CarType.SEDAN, 3)
                .containsEntry(CarType.SUV, 2);
    }
}