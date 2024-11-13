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
            .map(reservationMapper::toResponse);
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
    public List<Object[]> getReservationsByHourDistribution() {
        return reservationRepository.getReservationsByHourDistribution();
    }

    @Override
    public List<Object[]> getMostRequestedDepartureLocations() {
        return reservationRepository.findMostRequestedDepartureLocations();
    }

    @Override
    public ReservationAnalytics getAnalytics() {
        return ReservationAnalytics.builder()
            .averagePricePerKm(getAveragePricePerKm())
            .averageDistance(getAverageDistance())
            .hourlyDistribution(getReservationsByHourDistribution())
            .mostRequestedLocations(getMostRequestedDepartureLocations())
            .build();
    }

    private Reservation findReservationById(Long id) {
        return reservationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
    }
}