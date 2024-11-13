package com.fast_in.validation;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.fast_in.dto.request.ReservationRequest;
import com.fast_in.exception.ReservationException;
import com.fast_in.model.Reservation;
import com.fast_in.model.enums.StatutReservation;

@Component
public class ReservationValidator {
//
//    public void validateCreation(ReservationRequest request) {
//        validateDateTime(request.getDateHeure());
//        validateAddresses(request);
//        validateDistance(request.getDistanceKm());
//    }
//
//    public void validateStatusTransition(Reservation reservation, StatutReservation newStatus) {
//        StatutReservation currentStatus = reservation.getStatut();
//
//        if (currentStatus == StatutReservation.COMPLETED || currentStatus == StatutReservation.CANCELLED) {
//            throw new ReservationException("Cannot modify a completed or cancelled reservation");
//        }
//
//        if (currentStatus == StatutReservation.CREATED && newStatus == StatutReservation.COMPLETED) {
//            throw new ReservationException("Reservation must be confirmed before completion");
//        }
//    }
//
//    private void validateDateTime(LocalDateTime dateHeure) {
//        if (dateHeure.isBefore(LocalDateTime.now())) {
//            throw new ReservationException("Reservation date must be in the future");
//        }
//    }
//
//    private void validateAddresses(ReservationRequest request) {
//        if (request.getAdresseDepart().getVille().equals(request.getAdresseArrivee().getVille()) &&
//            request.getAdresseDepart().getQuartier().equals(request.getAdresseArrivee().getQuartier())) {
//            throw new ReservationException("Departure and arrival addresses cannot be the same");
//        }
//    }
//
//    private void validateDistance(Double distanceKm) {
//        if (distanceKm < 1 || distanceKm > 100) {
//            throw new ReservationException("Distance must be between 1 and 100 kilometers");
//        }
//    }
} 