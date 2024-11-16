package com.fast_in.service.impl;
import com.fast_in.dto.request.ReservationRequest;
import com.fast_in.dto.response.ReservationResponse;
import com.fast_in.model.Address;
import com.fast_in.model.enums.ReservationStatus;
import com.fast_in.model.enums.VehicleType;
import com.fast_in.service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ReservationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReservationService reservationService;

    private ReservationRequest validRequest;
    private ReservationResponse mockResponse;

    @BeforeEach
    void setUp() {
        // Setup test data
        validRequest = ReservationRequest.builder()
            .driverId(UUID.randomUUID())
            .vehicleId(UUID.randomUUID())
            .dateTime(LocalDateTime.now().plusHours(1))
            .distanceKm(10.0)
            .departureAddress(new Address("City1", "Area1"))
            .arrivalAddress(new Address("City2", "Area2"))
            .vehicleType(VehicleType.SEDAN)
            .build();

        mockResponse = ReservationResponse.builder()
            .id(UUID.randomUUID())
            .status(ReservationStatus.CREATED)
            .build();
    }

    @Test
    void createReservation_WithValidRequest_ShouldReturnCreated() throws Exception {
        when(reservationService.createReservation(any(ReservationRequest.class)))
            .thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    void getReservation_WithValidId_ShouldReturnReservation() throws Exception {
        UUID id = UUID.randomUUID();
        when(reservationService.getReservationById(id)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/reservations/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void checkDriverAvailability_ShouldReturnAvailabilityStatus() throws Exception {
        UUID driverId = UUID.randomUUID();
        LocalDateTime dateTime = LocalDateTime.now().plusHours(1);
        when(reservationService.checkDriverAvailability(driverId, dateTime))
            .thenReturn(true);

        mockMvc.perform(get("/api/v1/reservations/check/driver")
                .param("driverId", driverId.toString())
                .param("dateTime", dateTime.toString()))
            .andExpect(status().isOk())
            .andExpect(content().string("true"));
    }
}
