package com.fast_in.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import com.fast_in.model.Address;
import com.fast_in.model.Vehicle;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for creating or updating a reservation")
public class ReservationRequest {

    @Schema(description = "ID of the driver assigned to this reservation")
    @NotNull(message = "Driver ID is required")
    private Long driverId;

    @Schema(description = "ID of the vehicle assigned to this reservation")
    @NotNull(message = "Vehicle ID is required")
    private UUID vehicleId;

    @Schema(description = "Date and time of the reservation")
    @NotNull(message = "Date time is required")
    @FutureOrPresent(message = "Reservation date must be in present or future")
    private LocalDateTime dateTime;

    @Schema(description = "Distance of the journey in kilometers")
    @NotNull(message = "Distance is required")
    @DecimalMin(value = "1.0", message = "Distance must be at least 1 km")
    @DecimalMax(value = "100.0", message = "Distance cannot exceed 100 km")
    private Double distanceKm;

    @Schema(description = "Departure address details")
    @NotNull(message = "Departure address is required")
    @Valid
    private Address departureAddress;

    @Schema(description = "Arrival address details")
    @NotNull(message = "Arrival address is required")
    @Valid
    private Address arrivalAddress;

    @Schema(description = "Vehicle details")
    @NotNull(message = "Vehicle is required")
    @Valid
    private Vehicle vehicle;
    
}