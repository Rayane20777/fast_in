package com.fast_in.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fast_in.dto.request.ReservationRequest;
import com.fast_in.dto.response.ReservationResponse;
import com.fast_in.model.enums.ReservationStatus;
import com.fast_in.service.ReservationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservations", description = "Reservation management endpoints")
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping
    @Operation(summary = "Create a new reservation")
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest request) {
        return new ResponseEntity<>(reservationService.createReservation(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a reservation by ID")
    public ResponseEntity<ReservationResponse> getReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @GetMapping
    @Operation(summary = "Get all reservations with pagination")
    public ResponseEntity<Page<ReservationResponse>> getAllReservations(Pageable pageable) {
        return ResponseEntity.ok(reservationService.getAllReservations(pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing reservation")
    public ResponseEntity<ReservationResponse> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody ReservationRequest request) {
        return ResponseEntity.ok(reservationService.updateReservation(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a reservation")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get reservations by status")
    public ResponseEntity<List<ReservationResponse>> getReservationsByStatus(
            @PathVariable ReservationStatus status) {
        return ResponseEntity.ok(reservationService.getReservationsByStatus(status));
    }

    @GetMapping("/driver/{driverId}")
    @Operation(summary = "Get reservations by driver")
    public ResponseEntity<Page<ReservationResponse>> getReservationsByDriver(
            @PathVariable Long driverId,
            Pageable pageable) {
        return ResponseEntity.ok(reservationService.getReservationsByDriver(driverId, pageable));
    }

    @GetMapping("/vehicle/{vehicleId}")
    @Operation(summary = "Get reservations by vehicle")
    public ResponseEntity<Page<ReservationResponse>> getReservationsByVehicle(
            @PathVariable UUID vehicleId,
            Pageable pageable) {
        return ResponseEntity.ok(reservationService.getReservationsByVehicle(vehicleId, pageable));
    }

    @GetMapping("/dates")
    @Operation(summary = "Get reservations between dates")
    public ResponseEntity<List<ReservationResponse>> getReservationsBetweenDates(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        return ResponseEntity.ok(reservationService.getReservationsBetweenDates(startDate, endDate));
    }

    @GetMapping("/city/{city}")
    @Operation(summary = "Get reservations by city")
    public ResponseEntity<List<ReservationResponse>> getReservationsByCity(@PathVariable String city) {
        return ResponseEntity.ok(reservationService.getReservationsByCity(city));
    }

    // @GetMapping("/analytics")
    // @Operation(summary = "Get reservation analytics")
    // public ResponseEntity<ReservationAnalytics> getAnalytics() {
    //     return ResponseEntity.ok(ReservationAnalytics.builder()
    //         .averagePricePerKm(reservationService.getAveragePricePerKm())
    //         .averageDistance(reservationService.getAverageDistance())
    //         .mostRequestedLocations(reservationService.getMostRequestedDepartureLocations())
    //         .hourlyDistribution(reservationService.getReservationsByHourDistribution())
    //         .timeSlotDistribution(reservationService.getReservationsByTimeSlot())
    //         .pricePerKmByVehicleType(reservationService.getAveragePricePerKmByVehicleType())
    //         .build());
    // }

    @PatchMapping("/{id}/confirm")
    @Operation(summary = "Confirm a reservation")
    public ResponseEntity<ReservationResponse> confirmReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.confirmReservation(id));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel a reservation")
    public ResponseEntity<ReservationResponse> cancelReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.cancelReservation(id));
    }

    @PatchMapping("/{id}/complete")
    @Operation(summary = "Complete a reservation")
    public ResponseEntity<ReservationResponse> completeReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.completeReservation(id));
    }

    @GetMapping("/check/driver")
    @Operation(summary = "Check driver availability")
    public ResponseEntity<Boolean> checkDriverAvailability(
            @RequestParam Long driverId,
            @RequestParam LocalDateTime dateTime) {
        return ResponseEntity.ok(reservationService.checkDriverAvailability(driverId, dateTime));
    }

    @GetMapping("/check/vehicle")
    @Operation(summary = "Check vehicle availability")
    public ResponseEntity<Boolean> checkVehicleAvailability(
            @RequestParam UUID vehicleId,
            @RequestParam LocalDateTime dateTime) {
        return ResponseEntity.ok(reservationService.checkVehicleAvailability(vehicleId, dateTime));
    }
}