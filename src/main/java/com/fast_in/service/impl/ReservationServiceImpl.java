package com.fast_in.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fast_in.dto.request.ReservationRequest;
import com.fast_in.dto.response.ReservationAnalytics;
import com.fast_in.dto.response.ReservationResponse;
import com.fast_in.exception.ReservationException;
import com.fast_in.exception.ResourceNotFoundException;
import com.fast_in.mapper.ReservationMapper;
import com.fast_in.model.Driver;
import com.fast_in.model.Reservation;
import com.fast_in.model.Vehicle;
import com.fast_in.model.enums.DriverStatus;
import com.fast_in.model.enums.ReservationStatus;
import com.fast_in.model.enums.VehicleStatus;
import com.fast_in.repository.DriverRepository;
import com.fast_in.repository.ReservationRepository;
import com.fast_in.repository.VehicleRepository;
import com.fast_in.service.ReservationService;
import com.fast_in.utils.PriceCalculator;
import com.fast_in.validation.ReservationValidator;

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
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;

@Override
@Transactional
public ReservationResponse createReservation(ReservationRequest request) {
    log.info("Creating new reservation");

    // Validate availability
    if (!checkDriverAvailability(request.getDriverId(), request.getDateTime())) {
        throw new ReservationException("Driver is not available at the requested time");
    }
    if (!checkVehicleAvailability(request.getVehicleId(), request.getDateTime())) {
        throw new ReservationException("Vehicle is not available at the requested time");
    }

    // Find driver and vehicle
    Driver driver = driverRepository.findById(request.getDriverId())
        .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
    Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
        .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

 // Create and populate reservation
Reservation reservation = Reservation.builder()
.dateTime(request.getDateTime())
.distanceKm(request.getDistanceKm())
.departureAddress(request.getDepartureAddress())
.arrivalAddress(request.getArrivalAddress())
.driver(driver)
.vehicle(vehicle)
.courseStartTime(request.getDateTime())  // Set initial course time
.courseEndTime(request.getDateTime().plusHours(2))  // Assuming 2-hour duration
.build();

// Calculate price
double price = priceCalculator.calculatePrice(
request.getDistanceKm(),
request.getVehicleType(),
request.getDateTime()
);
reservation.setPrice(price);

    // Save and return
    Reservation savedReservation = reservationRepository.save(reservation);
    return reservationMapper.toResponse(savedReservation);
}

    @Override
    public ReservationResponse getReservationById(UUID id) {
        return reservationMapper.toResponse(findReservationById(id));
    }

    @Override
    public Page<ReservationResponse> getAllReservations(Pageable pageable) {
        return reservationRepository.findAll(pageable)
            .map(reservation -> reservationMapper.toResponse(reservation));
    }

    @Override
    @Transactional
    public ReservationResponse updateReservation(UUID id, ReservationRequest request) {
        log.info("Updating reservation {}", id);
        validator.validateCreation(request);

        Reservation reservation = findReservationById(id);

        // Check if the status needs to be updated
        if (request.getStatus() != null && request.getStatus() != reservation.getStatus()) {
            validator.validateStatusTransition(reservation, request.getStatus());
            reservation.setStatus(request.getStatus());
        }

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
    public void deleteReservation(UUID id) {
        Reservation reservation = findReservationById(id);
        if (reservation.getStatus() != ReservationStatus.CREATED) {
            throw new ReservationException("Can only delete reservations in CREATED status");
        }
        reservationRepository.delete(reservation);
        log.info("Deleted reservation {}", id);
    }

    @Override
    @Transactional
    public ReservationResponse confirmReservation(UUID id) {
        log.info("Confirming reservation {}", id);
        Reservation reservation = findReservationById(id);
        validator.validateStatusTransition(reservation, ReservationStatus.CONFIRMED);
        
        reservation.confirm();
        return reservationMapper.toResponse(reservationRepository.save(reservation));
    }

    @Override
    @Transactional
    public ReservationResponse cancelReservation(UUID id) {
        log.info("Cancelling reservation {}", id);
        Reservation reservation = findReservationById(id);
        validator.validateStatusTransition(reservation, ReservationStatus.CANCELLED);
        
        reservation.cancel();
        return reservationMapper.toResponse(reservationRepository.save(reservation));
    }

    @Override
    @Transactional
    public ReservationResponse completeReservation(UUID id) {
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
// Build location counts
List<ReservationAnalytics.LocationCount> locationCounts = reservationRepository
    .findMostRequestedDepartureLocations()
    .stream()
    .map(row -> ReservationAnalytics.LocationCount.builder()
        .location((String) row[0])
        .count(((Number) row[1]).intValue())
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
    public boolean checkDriverAvailability(UUID driverId, LocalDateTime dateTime) {
        log.info("Checking driver availability for ID: {} at {}", driverId, dateTime);
        
        // First check if driver exists and is available
        Driver driver = driverRepository.findById(driverId)
            .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + driverId));
        
        // Check if driver's status is AVAILABLE
        if (driver.getStatus() != DriverStatus.AVAILABLE) {
            log.info("Driver {} is not available (Status: {})", driverId, driver.getStatus());
            return false;
        }
        
        // Calculate time window (±2 hours around the requested time)
        LocalDateTime startTime = dateTime.minusHours(2);
        LocalDateTime endTime = dateTime.plusHours(2);
        
        // Check for overlapping active reservations
        return !reservationRepository.hasActiveReservations(driverId, startTime, endTime);
    }

    @Override
    public boolean checkVehicleAvailability(UUID vehicleId, LocalDateTime dateTime) {
        log.info("Checking vehicle availability for ID: {} at {}", vehicleId, dateTime);
        
        // First check if vehicle exists and is available
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + vehicleId));
        
        // Check if vehicle's status is AVAILABLE
        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
            log.info("Vehicle {} is not available (Status: {})", vehicleId, vehicle.getStatus());
            return false;
        }
        
        // Calculate time window (±2 hours around the requested time)
        LocalDateTime startTime = dateTime.minusHours(2);
        LocalDateTime endTime = dateTime.plusHours(2);
        
        // Check for overlapping active reservations
        return !reservationRepository.hasVehicleActiveReservations(vehicleId, startTime, endTime);
    }

    private Reservation findReservationById(UUID id) {
        return reservationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
    }
    public List<Reservation> findAllWithValidPromoCode() {
        return reservationRepository.findAll().stream()
            .filter(r -> r.getPromoCode() != null && !r.getPromoCode().isEmpty())
            .collect(Collectors.toList());
    }
}