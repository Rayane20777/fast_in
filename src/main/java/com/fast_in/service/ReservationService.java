package com.fast_in.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fast_in.dto.request.ReservationRequest;
import com.fast_in.dto.response.ReservationResponse;
import com.fast_in.model.enums.StatutReservation;

public interface ReservationService {
    ReservationResponse createReservation(ReservationRequest request);
    ReservationResponse getReservationById(Long id);
    Page<ReservationResponse> getAllReservations(Pageable pageable);
    ReservationResponse updateReservation(Long id, ReservationRequest request);
    void deleteReservation(Long id);
    
    List<ReservationResponse> getReservationsByStatus(StatutReservation statut);
    Page<ReservationResponse> getReservationsByDriver(Long chauffeurId, Pageable pageable);
    Page<ReservationResponse> getReservationsByVehicle(UUID vehiculeId, Pageable pageable);
    List<ReservationResponse> getReservationsBetweenDates(LocalDateTime debut, LocalDateTime fin);
    List<ReservationResponse> getReservationsByCity(String ville);
    
    // Analytics methods
    Double getAveragePricePerKm();
    Double getAverageDistance();
    List<Object[]> getMostRequestedDepartureLocations();
    
    // Business logic methods
    ReservationResponse confirmReservation(Long id);
    ReservationResponse cancelReservation(Long id);
    ReservationResponse completeReservation(Long id);
    boolean checkDriverAvailability(Long chauffeurId, LocalDateTime dateHeure);
    boolean checkVehicleAvailability(UUID vehiculeId, LocalDateTime dateHeure);
}
