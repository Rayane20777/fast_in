package com.fast_in.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.TreeMap;
import java.util.LinkedHashMap;
import java.util.HashMap;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fast_in.dao.ReservationDao;
import com.fast_in.dto.request.ReservationRequest;
import com.fast_in.dto.response.ReservationResponse;
import com.fast_in.exception.ReservationException;
import com.fast_in.exception.ResourceNotFoundException;
import com.fast_in.mapper.ReservationMapper;
import com.fast_in.model.Reservation;
import com.fast_in.model.enums.ReservationStatus;
import com.fast_in.repository.ReservationRepository;
import com.fast_in.service.ReservationService;
import com.fast_in.utils.PriceCalculator;
import com.fast_in.validation.ReservationValidator;
import com.fast_in.dto.response.ReservationAnalytics;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final ReservationValidator validator;
    private final PriceCalculator priceCalculator;

    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        log.info("Creating new reservation");
        validator.validateCreation(request);

        Reservation reservation = reservationMapper.toEntity(request);
        
        // Calculate price based on distance and vehicle type
        double price = priceCalculator.calculatePrice(
            request.getDistanceKm(),
            request.getVehicle().getType(),
            request.getDateTime()
        );
        reservation.setPrice(price);

        Reservation savedReservation = reservationRepository.save(reservation);
        log.info("Created reservation with ID: {}", savedReservation.getId());
        return reservationMapper.toResponse(savedReservation);
    }

    @Override
    public ReservationResponse getReservationById(Long id) {
        return reservationMapper.toResponse(findReservationById(id));
    }

    @Override
    public Page<ReservationResponse> getAllReservations(Pageable pageable) {
        return reservationRepository.findAll(pageable)
            .map(reservation -> reservationMapper.toResponse(reservation));
    }

    @Override
    @Transactional
    public ReservationResponse updateReservation(Long id, ReservationRequest request) {
        log.info("Updating reservation {}", id);
        validator.validateCreation(request);

        Reservation reservation = findReservationById(id);
        validator.validateStatusTransition(reservation, ReservationStatus.CREATED);

        reservationMapper.updateEntityFromRequest(reservation, request);

        // Recalculate price
        double price = priceCalculator.calculatePrice(
            request.getDistanceKm(),
            request.getVehicleType(),
            request.getDateTime()
        );
        reservation.setPrice(price);

        return reservationMapper.toResponse(reservationRepository.save(reservation));
    }

    @Override
    @Transactional
    public void deleteReservation(Long id) {
        Reservation reservation = findReservationById(id);
        if (reservation.getStatus() != ReservationStatus.CREATED) {
            throw new ReservationException("Can only delete reservations in CREATED status");
        }
        reservationRepository.delete(reservation);
        log.info("Deleted reservation {}", id);
    }

    @Override
    @Transactional
    public ReservationResponse confirmReservation(Long id) {
        log.info("Confirming reservation {}", id);
        Reservation reservation = findReservationById(id);
        validator.validateStatusTransition(reservation, ReservationStatus.CONFIRMED);
        
        reservation.confirm();
        return reservationMapper.toResponse(reservationRepository.save(reservation));
    }

    @Override
    @Transactional
    public ReservationResponse cancelReservation(Long id) {
        log.info("Cancelling reservation {}", id);
        Reservation reservation = findReservationById(id);
        validator.validateStatusTransition(reservation, ReservationStatus.CANCELLED);
        
        reservation.cancel();
        return reservationMapper.toResponse(reservationRepository.save(reservation));
    }

    @Override
    @Transactional
    public ReservationResponse completeReservation(Long id) {
        log.info("Completing reservation {}", id);
        Reservation reservation = findReservationById(id);
        validator.validateStatusTransition(reservation, ReservationStatus.COMPLETED);
        
        reservation.complete();
        return reservationMapper.toResponse(reservationRepository.save(reservation));
    }

    // Analytics methods
    @Override
    public Double getAveragePricePerKm() {
        return reservationRepository.calculateAveragePricePerKm();
    }

    @Override
    public Double getAverageDistance() {
        return reservationRepository.calculateAverageDistance();
    }

    @Override
    public Map<Integer, Integer> getReservationsByHourDistribution() {
        log.info("Fetching hourly distribution of reservations");
        return reservationRepository.getReservationsByHourDistribution()
            .stream()
            .collect(Collectors.toMap(
                row -> ((Number) row[0]).intValue(),
                row -> ((Number) row[1]).intValue(),
                (existing, replacement) -> existing,
                TreeMap::new
            ));
    }
    
    @Override
    public Map<String, Integer> getMostRequestedDepartureLocations() {
        log.info("Fetching most requested departure locations");
        return reservationRepository.findMostRequestedDepartureLocations()
            .stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> ((Number) row[1]).intValue(),
                (existing, replacement) -> existing,
                LinkedHashMap::new
            ));
    }
    @Override
    public ReservationAnalytics getAnalytics() {
        log.info("Generating reservation analytics");
        
        // Build hourly distribution map
        Map<Integer, Integer> hourlyDistribution = reservationRepository.getReservationsByHourDistribution()
            .stream()
            .collect(Collectors.toMap(
                row -> ((Number) row[0]).intValue(),
                row -> ((Number) row[1]).intValue(),
                (existing, replacement) -> existing,
                TreeMap::new  // Ensures ordered by hour
            ));

        // Build location counts
        List<ReservationAnalytics.LocationCount> locationCounts = reservationRepository
            .findMostRequestedDepartureLocations()
            .stream()
            .map(row -> ReservationAnalytics.LocationCount.builder()
                .location((String) row[0])
                .count(((Number) row[1]).longValue())
                .build())
            .collect(Collectors.toList());

        // Build time slot distribution
        Map<String, Integer> timeSlotDistribution = reservationRepository.getReservationsByTimeSlot()
            .stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> ((Number) row[1]).intValue(),
                (existing, replacement) -> existing,
                LinkedHashMap::new  // Preserves order
            ));

        // Build price per km by vehicle type
        Map<String, Double> pricePerKmByVehicleType = reservationRepository
            .getAveragePricePerKmByVehicleType()
            .stream()
            .collect(Collectors.toMap(
                row -> ((Enum<?>) row[0]).name(),
                row -> ((Number) row[1]).doubleValue(),
                (existing, replacement) -> existing,
                HashMap::new
            ));

        // Handle potential null values from averages
        Double avgPricePerKm = getAveragePricePerKm();
        Double avgDistance = getAverageDistance();

        return ReservationAnalytics.builder()
            .averagePricePerKm(avgPricePerKm != null ? avgPricePerKm : 0.0)
            .averageDistance(avgDistance != null ? avgDistance : 0.0)
            .hourlyDistribution(hourlyDistribution)
            .mostRequestedLocations(locationCounts)
            .timeSlotDistribution(timeSlotDistribution)
            .pricePerKmByVehicleType(pricePerKmByVehicleType)
            .build();
    }

    @Override
    public boolean checkDriverAvailability(Long driverId, LocalDateTime dateTime) {
        log.info("Checking driver availability for ID: {} at {}", driverId, dateTime);
        
        // Calculate time window (e.g., ±2 hours around the requested time)
        LocalDateTime startTime = dateTime.minusHours(2);
        LocalDateTime endTime = dateTime.plusHours(2);
        
        // Check for overlapping reservations
        List<Reservation> overlappingReservations = reservationRepository.findOverlappingReservations(
            driverId, startTime, endTime
        );
        
        return overlappingReservations.isEmpty();
    }

    @Override
    public boolean checkVehicleAvailability(UUID vehicleId, LocalDateTime dateTime) {
        log.info("Checking vehicle availability for ID: {} at {}", vehicleId, dateTime);
        
        // Calculate time window (e.g., ±2 hours around the requested time)
        LocalDateTime startTime = dateTime.minusHours(2);
        LocalDateTime endTime = dateTime.plusHours(2);
        
        // Use the existing repository method
        return !reservationRepository.hasActiveReservations(vehicleId, startTime, endTime);
    }

    private Reservation findReservationById(Long id) {
        return reservationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
    }
}