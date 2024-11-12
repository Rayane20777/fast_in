package com.fast_in.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fast_in.model.Reservation;
import com.fast_in.model.StatutReservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Basic CRUD methods are inherited from JpaRepository

    // Find reservations by status
    List<Reservation> findByStatut(StatutReservation statut);

    // Find reservations by driver ID
    Page<Reservation> findByChauffeurId(Long chauffeurId, Pageable pageable);

    // Find reservations by vehicle ID
    Page<Reservation> findByVehiculeId(Long vehiculeId, Pageable pageable);

    // Find reservations between dates
    List<Reservation> findByDateHeureBetween(LocalDateTime debut, LocalDateTime fin);

    // Find reservations by city
    @Query("SELECT r FROM Reservation r WHERE r.adresseDepart.ville = :ville OR r.adresseArrivee.ville = :ville")
    List<Reservation> findByVille(@Param("ville") String ville);

    // Analytics queries
    @Query("SELECT AVG(r.prix / r.distanceKm) FROM Reservation r WHERE r.statut = 'TERMINÉE'")
    Double calculateAveragePricePerKm();

    @Query("SELECT AVG(r.distanceKm) FROM Reservation r WHERE r.statut = 'TERMINÉE'")
    Double calculateAverageDistance();

    @Query("SELECT r.adresseDepart.ville as ville, COUNT(r) as count " +
           "FROM Reservation r " +
           "GROUP BY r.adresseDepart.ville " +
           "ORDER BY count DESC")
    List<Object[]> findMostRequestedDepartureLocations();

    // Find overlapping reservations for a driver
    @Query("SELECT r FROM Reservation r " +
           "WHERE r.chauffeur.id = :chauffeurId " +
           "AND r.statut IN ('CRÉÉE', 'CONFIRMÉE') " +
           "AND r.dateHeure BETWEEN :debut AND :fin")
    List<Reservation> findOverlappingReservations(
        @Param("chauffeurId") Long chauffeurId,
        @Param("debut") LocalDateTime debut,
        @Param("fin") LocalDateTime fin
    );

    // Count reservations by status for a specific driver
    @Query("SELECT r.statut, COUNT(r) " +
           "FROM Reservation r " +
           "WHERE r.chauffeur.id = :chauffeurId " +
           "GROUP BY r.statut")
    List<Object[]> countReservationsByStatusForDriver(@Param("chauffeurId") Long chauffeurId);

    // Find reservations by distance range
    List<Reservation> findByDistanceKmBetween(Double minDistance, Double maxDistance);

    // Check if a vehicle has active reservations
    @Query("SELECT COUNT(r) > 0 FROM Reservation r " +
           "WHERE r.vehicule.id = :vehiculeId " +
           "AND r.statut IN ('CRÉÉE', 'CONFIRMÉE')")
    boolean hasActiveReservations(@Param("vehiculeId") Long vehiculeId);
}
