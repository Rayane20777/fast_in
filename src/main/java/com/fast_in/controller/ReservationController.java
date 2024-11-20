package com.fast_in.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.FutureOrPresent;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fast_in.dto.request.ReservationRequest;
import com.fast_in.dto.response.ReservationAnalytics;
import com.fast_in.dto.response.ReservationResponse;
import com.fast_in.exception.ResourceNotFoundException;
import com.fast_in.model.Reservation;
import com.fast_in.model.enums.ReservationStatus;
import com.fast_in.service.ReservationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/reservations")
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
            @PathVariable UUID id) {
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
            @PathVariable UUID id,
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
    public ResponseEntity<Void> deleteReservation(@PathVariable UUID id) {
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
        @PathVariable UUID id,
        @RequestParam ReservationStatus status) {
    log.info("Updating reservation {} status to {}", id, status);
    
    try {
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
                throw new IllegalArgumentException("Invalid status update request. Status: " + status);
        }
        return ResponseEntity.ok(response);
    } catch (ResourceNotFoundException e) {
        log.error("Reservation not found: {}", e.getMessage());
        throw e;
    } catch (IllegalStateException e) {
        log.error("Invalid status transition: {}", e.getMessage());
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
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
            @RequestParam UUID driverId,
            @Parameter(description = "Check date and time", required = true)
            @RequestParam @FutureOrPresent LocalDateTime dateTime) {
        try {
            log.info("Checking availability for driver {} at {}", driverId, dateTime);
            return ResponseEntity.ok(reservationService.checkDriverAvailability(driverId, dateTime));
        } catch (ResourceNotFoundException e) {
            log.error("Driver not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error checking driver availability: {}", e.getMessage());
            throw new RuntimeException("Error checking driver availability", e);
        }
    }

    @GetMapping("/check/vehicle")
    @Operation(summary = "Check vehicle availability")
    public ResponseEntity<Boolean> checkVehicleAvailability(
            @Parameter(description = "Vehicle ID", required = true)
            @RequestParam UUID vehicleId,
            @Parameter(description = "Check date and time", required = true)
            @RequestParam @FutureOrPresent LocalDateTime dateTime) {
        try {
            log.info("Checking availability for vehicle {} at {}", vehicleId, dateTime);
            return ResponseEntity.ok(reservationService.checkVehicleAvailability(vehicleId, dateTime));
        } catch (ResourceNotFoundException e) {
            log.error("Vehicle not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error checking vehicle availability: {}", e.getMessage());
            throw new RuntimeException("Error checking vehicle availability", e);
        }
    }

       @GetMapping("/promos")
    public ResponseEntity<List<Reservation>> getReservationsWithPromo() {
        return ResponseEntity.ok(reservationService.findAllWithValidPromoCode());
    }

}