package com.fast_in.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fast_in.dto.request.ReservationRequest;
import com.fast_in.dto.response.ReservationResponse;
import com.fast_in.dto.response.ReservationAnalytics;

public interface ReservationService {
    // CRUD operations
    ReservationResponse createReservation(ReservationRequest request);
    ReservationResponse getReservationById(Long id);
    Page<ReservationResponse> getAllReservations(Pageable pageable);
    ReservationResponse updateReservation(Long id, ReservationRequest request);
    void deleteReservation(Long id);
    
    // Status management
    ReservationResponse confirmReservation(Long id);
    ReservationResponse cancelReservation(Long id);
    ReservationResponse completeReservation(Long id);
    
    // Analytics methods as specified in requirements
    Double getAveragePricePerKm();
    Double getAverageDistance();
    Map<Integer, Integer> getReservationsByHourDistribution();
    Map<String, Integer> getMostRequestedDepartureLocations();
    ReservationAnalytics getAnalytics();

    boolean checkDriverAvailability(Long driverId, LocalDateTime dateTime);
    boolean checkVehicleAvailability(UUID vehicleId, LocalDateTime dateTime);


}