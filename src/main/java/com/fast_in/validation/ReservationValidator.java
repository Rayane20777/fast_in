package com.fast_in.validation;

import org.springframework.stereotype.Component;

import com.fast_in.dto.request.ReservationRequest;
import com.fast_in.exception.ReservationException;
import com.fast_in.model.Reservation;
import com.fast_in.model.enums.ReservationStatus;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationValidator {

    public void validateCreation(ReservationRequest request) {
        if (request.getDateTime() == null) {
            throw new ReservationException("Reservation date time cannot be null");
        }
        
        if (request.getDistanceKm() == null || request.getDistanceKm() <= 0) {
            throw new ReservationException("Distance must be greater than 0");
        }
        
        if (request.getDepartureAddress() == null) {
            throw new ReservationException("Departure address is required");
        }
        
        if (request.getArrivalAddress() == null) {
            throw new ReservationException("Arrival address is required");
        }
    }

    public void validateStatusTransition(Reservation reservation, ReservationStatus newStatus) {
        ReservationStatus currentStatus = reservation.getStatus();
        
        if (currentStatus == newStatus) {
            throw new ReservationException("Reservation is already in " + newStatus + " status");
        }
        
        switch (newStatus) {
            case CONFIRMED:
                if (currentStatus != ReservationStatus.CREATED) {
                    throw new ReservationException("Only CREATED reservations can be confirmed");
                }
                break;
                
            case COMPLETED:
                if (currentStatus != ReservationStatus.CONFIRMED) {
                    throw new ReservationException("Only CONFIRMED reservations can be completed");
                }
                break;
                
            case CANCELLED:
                if (currentStatus == ReservationStatus.COMPLETED) {
                    throw new ReservationException("Cannot cancel a completed reservation");
                }
                break;
                
            default:
                throw new ReservationException("Invalid status transition");
        }
    }
}