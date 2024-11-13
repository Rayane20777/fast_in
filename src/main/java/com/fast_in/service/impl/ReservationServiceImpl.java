package com.fast_in.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.fast_in.model.Driver;
import com.fast_in.model.Vehicle;
import com.fast_in.model.enums.ReservationStatus;
import com.fast_in.repository.ReservationRepository;
import com.fast_in.service.DriverService;
import com.fast_in.service.ReservationService;
import com.fast_in.service.VehicleService;
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
    private final ReservationDao reservationDao;

    @Autowired(required = false)
    private DriverService driverService;

    @Autowired(required = false)
    private VehicleService vehicleService;

    @Value("${reservation.duration.default:2}")
    private int defaultReservationDuration;

    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        log.info("Creating new reservation");
        validator.validateCreation(request);

        Reservation reservation = reservationMapper.toEntity(request);
        
        // // Handle optional driver service
        // if (driverService != null) {
        //     Driver driver = driverService.findById(request.getDriverId());

        //     if (!checkDriverAvailability(driver.getId(), request.getDateTime())) {
        //         throw new ReservationException("Driver is not available at the requested time");
        //     }
        //     reservation.setDriver(driver);
        // }

        // Handle optional vehicle service
        if (vehicleService != null) {
            // Vehicle vehicle = vehicleService.findById(request.getVehicleId());

            Vehicle vehicle = null;
            if (!checkVehicleAvailability(vehicle.getId(), request.getDateTime())) {
                throw new ReservationException("Vehicle is not available at the requested time");
            }
            reservation.setVehicle(vehicle);
            
            // Calculate price only if vehicle is present
            double price = priceCalculator.calculatePrice(
                request.getDistanceKm(),
                vehicle.getType(),
                request.getDateTime()
            );
            reservation.setPrice(price);
        } else {
            // Set default price if no vehicle service
            reservation.setPrice(request.getDistanceKm() * 10.0); // Basic rate
        }

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
            .map(reservationMapper::toResponse);
    }

    @Override
    @Transactional
    public ReservationResponse updateReservation(Long id, ReservationRequest request) {
        log.info("Updating reservation {}", id);
        validator.validateCreation(request);

        Reservation reservation = findReservationById(id);
        validator.validateStatusTransition(reservation, ReservationStatus.CREATED);

        // Driver newDriver = driverService.findById(request.getDriverId());
        Driver newDriver = null;

        // Vehicle newVehicle = vehicleService.findById(request.getVehicleId());
        Vehicle newVehicle = null;

        // if (!checkDriverAvailability(newDriver.getId(), request.getDateTime())) {
        //     throw new ReservationException("New driver is not available at the requested time");
        // }

        if (!checkVehicleAvailability(newVehicle.getId(), request.getDateTime())) {
            throw new ReservationException("New vehicle is not available at the requested time");
        }

        reservationMapper.updateEntityFromRequest(reservation, request);
        reservation.setDriver(newDriver);
        reservation.setVehicle(newVehicle);

        double price = priceCalculator.calculatePrice(
            request.getDistanceKm(),
            newVehicle.getType(),
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
    public List<ReservationResponse> getReservationsByStatus(ReservationStatus status) {
        return reservationDao.findByStatus(status).stream()
            .map(reservationMapper::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    public Page<ReservationResponse> getReservationsByDriver(Long driverId, Pageable pageable) {
        return reservationDao.findByDriverId(driverId, pageable)
            .map(reservationMapper::toResponse);
    }

    @Override
    public Page<ReservationResponse> getReservationsByVehicle(UUID vehicleId, Pageable pageable) {
        return reservationDao.findByVehicleId(vehicleId, pageable)
            .map(reservationMapper::toResponse);
    }

    @Override
    public List<ReservationResponse> getReservationsBetweenDates(LocalDateTime start, LocalDateTime end) {
        return reservationDao.findByDateTimeBetween(start, end).stream()
            .map(reservationMapper::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<ReservationResponse> getReservationsByCity(String city) {
        return reservationDao.findByDepartureAddressCity(city).stream()
            .map(reservationMapper::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    public Double getAveragePricePerKm() {
        return reservationRepository.calculateAveragePricePerKm();
    }

    @Override
    public Double getAverageDistance() {
        return reservationRepository.calculateAverageDistance();
    }

    @Override
    public List<Object[]> getMostRequestedDepartureLocations() {
        return reservationRepository.findMostRequestedDepartureLocations();
    }

    @Override
    public List<Object[]> getReservationsByHourDistribution() {
        return reservationRepository.getReservationsByHourDistribution();
    }

    @Override
    public List<Object[]> getReservationsByTimeSlot() {
        return reservationRepository.getReservationsByTimeSlot();
    }

    @Override
    public List<Object[]> getAveragePricePerKmByVehicleType() {
        return reservationRepository.getAveragePricePerKmByVehicleType();
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

    @Override
    public boolean checkDriverAvailability(Long driverId, LocalDateTime dateTime) {
        if (driverService == null) {
            return true; // Always available if driver service is not present
        }
        LocalDateTime endTime = dateTime.plusHours(defaultReservationDuration);
        List<Reservation> overlappingReservations = reservationRepository
            .findOverlappingReservations(driverId, dateTime, endTime);
        return overlappingReservations.isEmpty();
    }

    @Override
    public boolean checkVehicleAvailability(UUID vehicleId, LocalDateTime dateTime) {
        if (vehicleService == null) {
            return true; // Always available if vehicle service is not present
        }
        LocalDateTime endTime = dateTime.plusHours(defaultReservationDuration);
        return !reservationRepository.hasActiveReservations(vehicleId, dateTime, endTime);
    }

    private Reservation findReservationById(Long id) {
        return reservationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
    }
}