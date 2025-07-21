package com.example.crs.repository;

import com.example.crs.entity.CarType;
import org.springframework.stereotype.Repository;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@Repository
public class CarRepository {

    private EnumMap<CarType, Integer> carInventory = new EnumMap<>(CarType.class);

    public CarRepository() {
        carInventory.put(CarType.VAN, 1);
        carInventory.put(CarType.SEDAN, 3);
        carInventory.put(CarType.SUV, 2);
    }

    public CarRepository(EnumMap<CarType, Integer> carInventory) {
        this.carInventory = carInventory;
    }

    public Map<CarType, Integer> getCarInventory() {
        return new HashMap<>(carInventory);
    }
}
