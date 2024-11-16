package com.fast_in.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.fast_in.dto.request.ReservationRequest;
import com.fast_in.dto.response.ReservationResponse;
import com.fast_in.model.Reservation;
import com.fast_in.model.enums.ReservationStatus;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationMapper {

  
    public Reservation toEntity(ReservationRequest request) {
        return Reservation.builder()
            .dateTime(request.getDateTime())
            .distanceKm(request.getDistanceKm())
            .departureAddress(request.getDepartureAddress())
            .arrivalAddress(request.getArrivalAddress())
            .courseStartTime(request.getDateTime())
            .courseEndTime(request.getDateTime().plusHours(2))
            .status(ReservationStatus.CREATED)
            .createdAt(LocalDateTime.now())
            .build();
    }

    public ReservationResponse toResponse(Reservation reservation) {
        return ReservationResponse.builder()
            .id(reservation.getId())
            .driverId(reservation.getDriver().getId())
            .vehicleId(reservation.getVehicle().getId())
            .dateTime(reservation.getDateTime())
            .distanceKm(reservation.getDistanceKm())
            .price(reservation.getPrice())
            .departureAddress(reservation.getDepartureAddress())
            .arrivalAddress(reservation.getArrivalAddress())
            .status(reservation.getStatus())
            .courseStartTime(reservation.getCourseStartTime())
            .courseEndTime(reservation.getCourseEndTime())
            .confirmedAt(reservation.getConfirmedAt())
            .completedAt(reservation.getCompletedAt())
            .cancelledAt(reservation.getCancelledAt())
            .createdAt(reservation.getCreatedAt())
            .updatedAt(reservation.getUpdatedAt())
            .build();
    }

    public void updateEntityFromRequest(Reservation reservation, ReservationRequest request) {
        reservation.setDateTime(request.getDateTime());
        reservation.setDistanceKm(request.getDistanceKm());
        reservation.setDepartureAddress(request.getDepartureAddress());
        reservation.setArrivalAddress(request.getArrivalAddress());
    }
}