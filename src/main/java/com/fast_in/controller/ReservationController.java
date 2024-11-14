package com.fast_in.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.fast_in.dto.request.ReservationRequest;
import com.fast_in.dto.response.ReservationAnalytics;
import com.fast_in.dto.response.ReservationResponse;
import com.fast_in.model.enums.ReservationStatus;
import com.fast_in.service.ReservationService;

import javax.validation.Valid;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Reservations", description = "Reservation management API")
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping
    @Operation(summary = "Create a new reservation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reservation created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "409", description = "Reservation conflict")
    })
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationRequest request) {
        log.info("Creating new reservation");
        return new ResponseEntity<>(reservationService.createReservation(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a reservation by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Found the reservation"),
        @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    public ResponseEntity<ReservationResponse> getReservation(
            @Parameter(description = "ID of the reservation", required = true)
            @PathVariable Long id) {
        log.info("Fetching reservation with id: {}", id);
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @GetMapping
    @Operation(summary = "Get all reservations with pagination")
    @ApiResponse(responseCode = "200", description = "List of reservations")
    public ResponseEntity<Page<ReservationResponse>> getAllReservations(
            @PageableDefault(size = 20, sort = "dateTime") Pageable pageable) {
        log.info("Fetching page {} of reservations", pageable.getPageNumber());
        return ResponseEntity.ok(reservationService.getAllReservations(pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing reservation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation updated"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    public ResponseEntity<ReservationResponse> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody ReservationRequest request) {
        log.info("Updating reservation with id: {}", id);
        return ResponseEntity.ok(reservationService.updateReservation(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a reservation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Reservation deleted"),
        @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        log.info("Deleting reservation with id: {}", id);
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update reservation status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status updated"),
    @ApiResponse(responseCode = "400", description = "Invalid status transition"),
    @ApiResponse(responseCode = "404", description = "Reservation not found")
})
public ResponseEntity<ReservationResponse> updateStatus(
        @PathVariable Long id,
        @RequestParam ReservationStatus status) {
    log.info("Updating status of reservation {} to {}", id, status);
    ReservationResponse response;
    switch (status) {
        case CONFIRMED:
            response = reservationService.confirmReservation(id);
            break;
        case CANCELLED:
            response = reservationService.cancelReservation(id);
            break;
        case COMPLETED:
            response = reservationService.completeReservation(id);
            break;
        default:
            throw new IllegalArgumentException("Invalid status: " + status);
    }
    return ResponseEntity.ok(response);
}

    @GetMapping("/analytics")
    @Operation(summary = "Get reservation analytics")
    @ApiResponse(responseCode = "200", description = "Analytics data retrieved")
    public ResponseEntity<ReservationAnalytics> getAnalytics() {
        log.info("Fetching reservation analytics");
        return ResponseEntity.ok(reservationService.getAnalytics());
    }

    @GetMapping("/check/driver")
    @Operation(summary = "Check driver availability")
    public ResponseEntity<Boolean> checkDriverAvailability(
            @Parameter(description = "Driver ID", required = true)
            @RequestParam Long driverId,
            @Parameter(description = "Check date and time", required = true)
            @RequestParam @FutureOrPresent LocalDateTime dateTime) {
        log.info("Checking availability for driver {} at {}", driverId, dateTime);
        return ResponseEntity.ok(reservationService.checkDriverAvailability(driverId, dateTime));
    }

    @GetMapping("/check/vehicle")
    @Operation(summary = "Check vehicle availability")
    public ResponseEntity<Boolean> checkVehicleAvailability(
            @Parameter(description = "Vehicle ID", required = true)
            @RequestParam UUID vehicleId,
            @Parameter(description = "Check date and time", required = true)
            @RequestParam @FutureOrPresent LocalDateTime dateTime) {
        log.info("Checking availability for vehicle {} at {}", vehicleId, dateTime);
        return ResponseEntity.ok(reservationService.checkVehicleAvailability(vehicleId, dateTime));
    }
}