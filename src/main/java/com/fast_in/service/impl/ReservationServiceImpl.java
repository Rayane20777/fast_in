package com.fast_in.service.impl;

import com.fast_in.dto.request.ReservationRequest;
import com.fast_in.dto.response.ReservationResponse;
import com.fast_in.exception.ReservationException;
import com.fast_in.exception.ResourceNotFoundException;
import com.fast_in.mapper.ReservationMapper;
import com.fast_in.model.Reservation;
import com.fast_in.model.enums.StatutReservation;
import com.fast_in.repository.ReservationRepository;
import com.fast_in.service.DriverService;
import com.fast_in.service.ReservationService;
import com.fast_in.service.VehicleService;
import com.fast_in.validation.ReservationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final ReservationValidator validator;
    private final DriverService driverService;
    private final VehicleService vehicleService;

    @Value("${reservation.price.base-rate}")
    private double baseRate;

    @Value("${reservation.price.per-km-rate}")
    private double perKmRate;

    @Override
    public ReservationResponse createReservation(ReservationRequest request) {
        try {
            validator.validateCreation(request);
            // checkDriverAvailability(request.getChauffeurId(), request.getDateHeure());
            checkVehicleAvailability(request.getVehiculeId(), request.getDateHeure());

            Reservation reservation = reservationMapper.toEntity(request);
            reservationMapper.updatePrix(reservation, baseRate, perKmRate);
            
            return reservationMapper.toResponse(reservationRepository.save(reservation));
        } catch (Exception e) {
            throw new ReservationException("Failed to create reservation: " + e.getMessage(), e);
        }
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
        Reservation reservation = findReservationById(id);
        validateReservationStatus(reservation, StatutReservation.CREATED);
        
        // checkDriverAvailability(request.getChauffeurId(), request.getDateHeure());
        checkVehicleAvailability(request.getVehiculeId(), request.getDateHeure());
        
        reservationMapper.updateEntityFromRequest(request, reservation);
        reservationMapper.updatePrix(reservation, baseRate, perKmRate);
        
        return reservationMapper.toResponse(reservationRepository.save(reservation));
    }

    @Override
    public void deleteReservation(Long id) {
        Reservation reservation = findReservationById(id);
        validateReservationStatus(reservation, StatutReservation.CREATED);
        reservationRepository.deleteById(id);
    }

    @Override
    public List<ReservationResponse> getReservationsByStatus(StatutReservation statut) {
        return reservationRepository.findByStatut(statut).stream()
                .map(reservationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ReservationResponse> getReservationsByDriver(Long chauffeurId, Pageable pageable) {
        return reservationRepository.findByChauffeurId(chauffeurId, pageable)
                .map(reservationMapper::toResponse);
    }

    @Override
    public Page<ReservationResponse> getReservationsByVehicle(UUID vehiculeId, Pageable pageable) {
        return reservationRepository.findByVehiculeId(vehiculeId, pageable)
                .map(reservationMapper::toResponse);
    }

    @Override
    public List<ReservationResponse> getReservationsBetweenDates(LocalDateTime debut, LocalDateTime fin) {
        return reservationRepository.findByDateHeureBetween(debut, fin).stream()
                .map(reservationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationResponse> getReservationsByCity(String ville) {
        return reservationRepository.findByVille(ville).stream()
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
    @Transactional
    public ReservationResponse confirmReservation(Long id) {
        try {
            Reservation reservation = findReservationById(id);
            validator.validateStatusTransition(reservation, StatutReservation.CONFIRMED);
            reservation.setStatut(StatutReservation.CONFIRMED);
            return reservationMapper.toResponse(reservationRepository.save(reservation));
        } catch (Exception e) {
            throw new ReservationException("Failed to confirm reservation: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public ReservationResponse cancelReservation(Long id) {
        Reservation reservation = findReservationById(id);
        if (reservation.getStatut() == StatutReservation.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a completed reservation");
        }
        reservation.setStatut(StatutReservation.CANCELLED);
        return reservationMapper.toResponse(reservationRepository.save(reservation));
    }

    @Override
    @Transactional
    public ReservationResponse completeReservation(Long id) {
        Reservation reservation = findReservationById(id);
        validateReservationStatus(reservation, StatutReservation.CONFIRMED);
        reservation.setStatut(StatutReservation.COMPLETED);
        return reservationMapper.toResponse(reservationRepository.save(reservation));
    }

    // @Override
    // public boolean checkDriverAvailability(Long chauffeurId, LocalDateTime dateHeure) {
    //     LocalDateTime endTime = dateHeure.plusHours(2); // Assuming 2-hour reservation duration
    //     List<Reservation> overlapping = reservationRepository.findOverlappingReservations(
    //         chauffeurId, dateHeure, endTime);
        
    //     if (!overlapping.isEmpty()) {
    //         throw new IllegalStateException("Driver is not available at the requested time");
    //     }
        
    //     return driverService.isAvailable(chauffeurId, dateHeure);
    // }

    @Override
    public boolean checkVehicleAvailability(UUID vehiculeId, LocalDateTime dateHeure) {
        if (reservationRepository.hasActiveReservations(vehiculeId)) {
            throw new IllegalStateException("Vehicle is not available at the requested time");
        }
        
        return vehicleService.isAvailable(vehiculeId, dateHeure);
    }

    private Reservation findReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
    }

    private void validateReservationStatus(Reservation reservation, StatutReservation expectedStatus) {
        if (reservation.getStatut() != expectedStatus) {
            throw new IllegalStateException(
                "Invalid reservation status. Expected: " + expectedStatus + 
                ", but was: " + reservation.getStatut());
        }
    }
} 