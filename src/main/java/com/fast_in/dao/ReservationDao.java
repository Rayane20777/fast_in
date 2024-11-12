package com.fast_in.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fast_in.model.Reservation;
import com.fast_in.model.enums.StatutReservation;

@Repository
public interface ReservationDao extends JpaRepository<Reservation, Long> {
    // Basic finder methods
    List<Reservation> findByStatut(StatutReservation statut);
    Page<Reservation> findBydriverId(Long driverId, Pageable pageable);
    Page<Reservation> findByVehiculeId(UUID vehiculeId, Pageable pageable);
    List<Reservation> findByDateHeureBetween(LocalDateTime debut, LocalDateTime fin);
    List<Reservation> findByDistanceKmBetween(Double minDistance, Double maxDistance);
}
