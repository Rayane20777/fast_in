package com.fast_in.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fast_in.model.Address;
import com.fast_in.model.enums.ReservationStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing reservation details")
public class ReservationResponse {
    private Long id;
    private Long driverId;
    private UUID vehicleId;
    private LocalDateTime dateTime;
    private Address departureAddress;
    private Address arrivalAddress;
    private Double price;
    private Double distanceKm;
    private ReservationStatus status;
    private LocalDateTime courseStartTime;
    private LocalDateTime courseEndTime;
    private LocalDateTime confirmedAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
