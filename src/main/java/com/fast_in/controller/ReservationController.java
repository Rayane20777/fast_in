package com.fast_in.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fast_in.dto.request.ReservationRequest;
import com.fast_in.dto.response.ReservationResponse;
import com.fast_in.model.enums.StatutReservation;
import com.fast_in.service.ReservationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest request) {
        return new ResponseEntity<>(reservationService.createReservation(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @GetMapping
    public ResponseEntity<Page<ReservationResponse>> getAllReservations(Pageable pageable) {
        return ResponseEntity.ok(reservationService.getAllReservations(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationResponse> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody ReservationRequest request) {
        return ResponseEntity.ok(reservationService.updateReservation(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{statut}")
    public ResponseEntity<List<ReservationResponse>> getReservationsByStatus(
            @PathVariable StatutReservation statut) {
        return ResponseEntity.ok(reservationService.getReservationsByStatus(statut));
    }

    @GetMapping("/driver/{chauffeurId}")
    public ResponseEntity<Page<ReservationResponse>> getReservationsByDriver(
            @PathVariable Long chauffeurId,
            Pageable pageable) {
        return ResponseEntity.ok(reservationService.getReservationsByDriver(chauffeurId, pageable));
    }

    @GetMapping("/vehicle/{vehiculeId}")
    public ResponseEntity<Page<ReservationResponse>> getReservationsByVehicle(
            @PathVariable UUID vehiculeId,
            Pageable pageable) {
        return ResponseEntity.ok(reservationService.getReservationsByVehicle(vehiculeId, pageable));
    }

    @GetMapping("/dates")
    public ResponseEntity<List<ReservationResponse>> getReservationsBetweenDates(
            @RequestParam LocalDateTime debut,
            @RequestParam LocalDateTime fin) {
        return ResponseEntity.ok(reservationService.getReservationsBetweenDates(debut, fin));
    }

    @GetMapping("/city/{ville}")
    public ResponseEntity<List<ReservationResponse>> getReservationsByCity(@PathVariable String ville) {
        return ResponseEntity.ok(reservationService.getReservationsByCity(ville));
    }

    @GetMapping("/analytics/average-price")
    public ResponseEntity<Double> getAveragePricePerKm() {
        return ResponseEntity.ok(reservationService.getAveragePricePerKm());
    }

    @GetMapping("/analytics/average-distance")
    public ResponseEntity<Double> getAverageDistance() {
        return ResponseEntity.ok(reservationService.getAverageDistance());
    }

    @GetMapping("/analytics/popular-locations")
    public ResponseEntity<List<Object[]>> getMostRequestedDepartureLocations() {
        return ResponseEntity.ok(reservationService.getMostRequestedDepartureLocations());
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<ReservationResponse> confirmReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.confirmReservation(id));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ReservationResponse> cancelReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.cancelReservation(id));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<ReservationResponse> completeReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.completeReservation(id));
    }
}
