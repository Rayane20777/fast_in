package com.fast_in.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fast_in.model.Reservation;

@Repository
public interface ReservationRepository {
    // Complex queries for analytics
    @Query("SELECT AVG(r.prix / r.distanceKm) FROM Reservation r WHERE r.statut = 'TERMINÉE'")
    Double calculateAveragePricePerKm();

    @Query("SELECT AVG(r.distanceKm) FROM Reservation r WHERE r.statut = 'TERMINÉE'")
    Double calculateAverageDistance();

    @Query("SELECT r.adresseDepart.ville as ville, COUNT(r) as count " +
           "FROM Reservation r " +
           "GROUP BY r.adresseDepart.ville " +
           "ORDER BY count DESC")
    List<Object[]> findMostRequestedDepartureLocations();

    // Complex business logic queries
    @Query("SELECT r FROM Reservation r " +
           "WHERE r.chauffeur.id = :driverId " +
           "AND r.statut IN ('CRÉÉE', 'CONFIRMÉE') " +
           "AND r.dateHeure BETWEEN :debut AND :fin")
    List<Reservation> findOverlappingReservations(
        @Param("driverId") Long driverId,
        @Param("debut") LocalDateTime debut,
        @Param("fin") LocalDateTime fin
    );

    @Query("SELECT r.statut, COUNT(r) " +
           "FROM Reservation r " +
           "WHERE r.chauffeur.id = :driverId " +
           "GROUP BY r.statut")
    List<Object[]> countReservationsByStatusForDriver(@Param("driverId") Long driverId);

    @Query("SELECT COUNT(r) > 0 FROM Reservation r " +
           "WHERE r.vehicle.id = :vehiculeId " +
           "AND r.statut IN ('CRÉÉE', 'CONFIRMÉE')")
    boolean hasActiveReservations(@Param("vehiculeId") UUID vehiculeId);

    @Query("SELECT r FROM Reservation r WHERE r.adresseDepart.ville = :ville OR r.adresseArrivee.ville = :ville")
    List<Reservation> findByVille(@Param("ville") String ville);
}
