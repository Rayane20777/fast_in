package com.fast_in.integration;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.fast_in.dto.request.ReservationRequest;
import com.fast_in.dto.response.ReservationResponse;
import com.fast_in.model.enums.StatutReservation;
import com.fast_in.service.ReservationService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ReservationIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ReservationService reservationService;

    @Test
    void createReservation_Success() {
        ReservationRequest request = createValidReservationRequest();
        
        ResponseEntity<ReservationResponse> response = restTemplate.postForEntity(
            "/api/reservations", request, ReservationResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatut()).isEqualTo(StatutReservation.CREATED);
    }

    @Test
    void getReservation_NotFound() {
        ResponseEntity<ReservationResponse> response = restTemplate.getForEntity(
            "/api/reservations/999", ReservationResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void completeReservationFlow_Success() {
        // Create
        ReservationRequest request = createValidReservationRequest();
        ResponseEntity<ReservationResponse> createResponse = restTemplate.postForEntity(
            "/api/reservations", request, ReservationResponse.class);
        Long reservationId = createResponse.getBody().getId();

        // Confirm
        restTemplate.put("/api/reservations/" + reservationId + "/confirm", null);
        ResponseEntity<ReservationResponse> confirmedResponse = restTemplate.getForEntity(
            "/api/reservations/" + reservationId, ReservationResponse.class);
        assertThat(confirmedResponse.getBody().getStatut()).isEqualTo(StatutReservation.CONFIRMED);

        // Complete
        restTemplate.put("/api/reservations/" + reservationId + "/complete", null);
        ResponseEntity<ReservationResponse> completedResponse = restTemplate.getForEntity(
            "/api/reservations/" + reservationId, ReservationResponse.class);
        assertThat(completedResponse.getBody().getStatut()).isEqualTo(StatutReservation.COMPLETED);
    }

    private ReservationRequest createValidReservationRequest() {
        ReservationRequest request = new ReservationRequest();
        request.setDateHeure(LocalDateTime.now().plusDays(1));
        request.setVehiculeId(UUID.randomUUID());
        // request.setDriverId(UUID.randomUUID());
        request.setDistanceKm(10.0);
        // Set other required fields
        return request;
    }
} 