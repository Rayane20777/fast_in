package com.fast_in.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fast_in.model.Reservation;
import com.fast_in.model.enums.ReservationStatus;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    
    Page<Reservation> findByStatus(ReservationStatus status, Pageable pageable);
    
    @Query("SELECT r FROM Reservation r WHERE r.driver.id = :driverId")
    Page<Reservation> findByDriverId(@Param("driverId") UUID driverId, Pageable pageable);
    
    @Query("SELECT r FROM Reservation r WHERE r.vehicle.id = :vehicleId")
    Page<Reservation> findByVehicleId(@Param("vehicleId") UUID vehicleId, Pageable pageable);

    // Availability check queries
    @Query("SELECT COUNT(r) > 0 FROM Reservation r " +
           "WHERE r.driver.id = :driverId " +
           "AND r.status NOT IN ('COMPLETED', 'CANCELLED') " +
           "AND :dateTime BETWEEN r.dateTime AND r.courseEndTime")
    boolean hasDriverConflictingReservation(
        @Param("driverId") UUID driverId, 
        @Param("dateTime") LocalDateTime dateTime
    );

    @Query("SELECT COUNT(r) > 0 FROM Reservation r " +
           "WHERE r.vehicle.id = :vehicleId " +
           "AND r.status NOT IN ('COMPLETED', 'CANCELLED') " +
           "AND :dateTime BETWEEN r.dateTime AND r.courseEndTime")
    boolean hasVehicleConflictingReservation(
        @Param("vehicleId") UUID vehicleId, 
        @Param("dateTime") LocalDateTime dateTime
    );

    // Analytics queries
    @Query("SELECT AVG(r.price / r.distanceKm) FROM Reservation r WHERE r.status = 'COMPLETED'")
    Double calculateAveragePricePerKm();
    
    @Query("SELECT AVG(r.distanceKm) FROM Reservation r WHERE r.status = 'COMPLETED'")
    Double calculateAverageDistance();
    
    @Query("SELECT r.departureAddress.ville, COUNT(r) FROM Reservation r " +
           "GROUP BY r.departureAddress.ville ORDER BY COUNT(r) DESC")
    List<Object[]> findMostRequestedDepartureLocations();
    
    @Query("SELECT HOUR(r.dateTime), COUNT(r) FROM Reservation r " +
           "GROUP BY HOUR(r.dateTime) ORDER BY HOUR(r.dateTime)")
    List<Object[]> getReservationsByHourDistribution();
    
    @Query("SELECT " +
           "CASE " +
           "  WHEN HOUR(r.dateTime) BETWEEN 6 AND 10 THEN 'Morning' " +
           "  WHEN HOUR(r.dateTime) BETWEEN 11 AND 14 THEN 'Noon' " +
           "  WHEN HOUR(r.dateTime) BETWEEN 15 AND 18 THEN 'Afternoon' " +
           "  WHEN HOUR(r.dateTime) BETWEEN 19 AND 22 THEN 'Evening' " +
           "  ELSE 'Night' " +
           "END as timeSlot, COUNT(r) " +
           "FROM Reservation r GROUP BY timeSlot ORDER BY timeSlot")
    List<Object[]> getReservationsByTimeSlot();
    
    @Query("SELECT r.vehicle.type, AVG(r.price / r.distanceKm) FROM Reservation r " +
           "WHERE r.status = 'COMPLETED' GROUP BY r.vehicle.type")
    List<Object[]> getAveragePricePerKmByVehicleType();
    
    @Query("SELECT r.vehicle.type as type, COUNT(r) as count FROM Reservation r " +
           "GROUP BY r.vehicle.type")
    List<Object[]> getReservationsByVehicleType();

    // Overlapping reservations check
    @Query("SELECT r FROM Reservation r " +
           "WHERE r.driver.id = :driverId " +
           "AND r.status NOT IN ('COMPLETED', 'CANCELLED') " +
           "AND ((r.dateTime BETWEEN :startTime AND :endTime) OR " +
           "(r.courseEndTime BETWEEN :startTime AND :endTime))")
    List<Reservation> findOverlappingReservations(
        @Param("driverId") UUID driverId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    @Query("SELECT COUNT(r) > 0 FROM Reservation r " +
           "WHERE r.driver.id = :driverId " +
           "AND r.status NOT IN ('COMPLETED', 'CANCELLED') " +
           "AND ((r.dateTime BETWEEN :startTime AND :endTime) OR " +
           "(r.courseEndTime BETWEEN :startTime AND :endTime))")
    boolean hasActiveReservations(
        @Param("driverId") UUID driverId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    @Query("SELECT COUNT(r) > 0 FROM Reservation r " +
           "WHERE r.vehicle.id = :vehicleId " +
           "AND r.status NOT IN ('COMPLETED', 'CANCELLED') " +
           "AND ((r.dateTime BETWEEN :startTime AND :endTime) OR " +
           "(r.courseEndTime BETWEEN :startTime AND :endTime))")
    boolean hasVehicleActiveReservations(
        @Param("vehicleId") UUID vehicleId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
}