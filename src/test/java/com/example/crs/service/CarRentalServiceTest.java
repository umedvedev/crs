package com.example.crs.service;

import com.example.crs.entity.CarType;
import com.example.crs.entity.Reservation;
import com.example.crs.exception.RentException;
import com.example.crs.repository.CarRepository;
import com.example.crs.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import static com.example.crs.entity.CarType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarRentalServiceTest {
    private static final LocalDateTime TOMORROW = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime YESTERDAY = LocalDateTime.now().minusDays(1);

    @Mock
    private CarRepository carRepository;

    @Spy
    private ReservationRepository reservationRepository;

    @InjectMocks
    private CarRentalService testee;


    @Test
    void makeReservationInPastFailed() {
        //when
        RentException iae = assertThrows(RentException.class, () -> testee.makeReservation(VAN, YESTERDAY, 1));
        //then
        assertThat(iae.getMessage()).isEqualTo("Start date and time must not be in the past");
        verifyNoInteractions(carRepository);
        verifyNoInteractions(reservationRepository);
    }

    @Test
    void makeReservationSuccess() {
        //given
        when(carRepository.getCarInventory()).thenReturn(Map.of(
                VAN, 1,
                SEDAN, 1,
                SUV, 1
        ));
        //when
        Arrays.stream(CarType.values())
                .forEach(t -> {
                    Reservation reservation = testee.makeReservation(t, TOMORROW, 1);
                    assertThat(reservation).isNotNull()
                            .hasFieldOrPropertyWithValue("type", t)
                            .hasFieldOrPropertyWithValue("startDate", TOMORROW)
                            .hasFieldOrPropertyWithValue("endDate", TOMORROW.plusDays(1));
                });
        Arrays.stream(CarType.values())
                .forEach(t -> {
                    RentException ise = assertThrows(RentException.class, () -> testee.makeReservation(t, TOMORROW, 1));
                    assertThat(ise).matches(e -> e.getMessage().contains("No available cars of type " + t + " for the requested period"));
                });
        //then
        verify(carRepository, times(6)).getCarInventory();
        verify(reservationRepository, times(3)).addReservation(any(Reservation.class));
    }

    @Test
    void makeReservationIntersectedFailed() {
        //when
        when(carRepository.getCarInventory()).thenReturn(Map.of(SUV, 1));
        when(reservationRepository.getReservationsByType(SUV)).thenReturn(Collections.singletonList(
                new Reservation(SUV, TOMORROW, TOMORROW.plusDays(2))
        ));

        RentException re = assertThrows(RentException.class, () -> testee.makeReservation(SUV, TOMORROW.plusDays(1), 5));
        assertThat(re).matches(e -> e.getMessage().contains("No available cars of type SUV for the requested period"));
    }

    @ParameterizedTest
    @MethodSource("provide")
    void makeReservationInvalidInputFailed(
            CarType type,
            LocalDateTime startDateTime,
            int days,
            String errorMessage
    ) {
        //when
        RentException re = assertThrows(RentException.class, () -> testee.makeReservation(type, startDateTime, days));
        //then
        assertThat(re.getMessage()).isEqualTo(errorMessage);
    }

    public static Stream<Arguments> provide() {
        return Stream.of(
                Arguments.of(null, TOMORROW, 1, "Car type must not be null"),
                Arguments.of(VAN, null, 1, "Start date and time must not be null"),
                Arguments.of(VAN, TOMORROW, 0, "Days must be greater than zero"),
                Arguments.of(VAN, YESTERDAY, 1, "Start date and time must not be in the past")
        );
    }

}