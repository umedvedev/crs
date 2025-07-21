package com.example.crs.controller;

import com.example.crs.entity.CarType;
import com.example.crs.entity.Reservation;
import com.example.crs.service.CarRentalService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static com.example.crs.entity.CarType.VAN;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CarRentalController.class)
class CarRentalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarRentalService carRentalService;

    @Test
    @DisplayName("Should rent a car successfully")
    void rentCar() throws Exception {
        //given
        CarType type = CarType.SEDAN;
        LocalDateTime dateTime = LocalDateTime.now().plusDays(5);
        int days = 3;
        Reservation reservation = new Reservation(type, dateTime, dateTime.plusDays(days));
        when(carRentalService.makeReservation(type, dateTime, days)).thenReturn(reservation);
        //when
        mockMvc.perform(put("/rent")
                .param("type", type.name())
                .param("dateTime", dateTime.toString())
                .param("days", String.valueOf(days))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return error for invalid input - days")
    void rentCarInvalidInputDays() throws Exception {
        //given
        CarType type = VAN;
        LocalDateTime dateTime = LocalDateTime.now().plusDays(5);
        int days = 0;
        when(carRentalService.makeReservation(type, dateTime, days)).thenCallRealMethod();
        //when
        mockMvc.perform(put("/rent")
                        .param("type", type.name())
                        .param("dateTime", dateTime.toString())
                        .param("days", String.valueOf(days))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

    }
}