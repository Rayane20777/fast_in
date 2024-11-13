package com.fast_in.dao;

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
public interface ReservationDao extends JpaRepository<Reservation, Long> {
    // Basic finder methods with improved naming
    List<Reservation> findByStatus(ReservationStatus status);
    
    @Query("SELECT r FROM Reservation r WHERE r.driver.id = :driverId")
    Page<Reservation> findByDriverId(@Param("driverId") Long driverId, Pageable pageable);
    
    @Query("SELECT r FROM Reservation r WHERE r.vehicle.id = :vehicleId")
    Page<Reservation> findByVehicleId(@Param("vehicleId") UUID vehicleId, Pageable pageable);
    
    @Query("SELECT r FROM Reservation r WHERE r.dateTime BETWEEN :start AND :end")
    List<Reservation> findByDateTimeBetween(
        @Param("start") LocalDateTime start, 
        @Param("end") LocalDateTime end
    );
    
    // Advanced search methods
    @Query("SELECT r FROM Reservation r WHERE r.distanceKm BETWEEN :min AND :max")
    List<Reservation> findByDistanceRange(
        @Param("min") Double minDistance, 
        @Param("max") Double maxDistance
    );
    
    @Query("SELECT r FROM Reservation r " +
           "WHERE r.departureAddress.ville = :city " +
           "OR r.arrivalAddress.ville = :city")
    Page<Reservation> findByCity(@Param("city") String city, Pageable pageable);
    
    @Query("SELECT r FROM Reservation r WHERE r.departureAddress.ville = :city")
    List<Reservation> findByDepartureAddressCity(@Param("city") String city);
    
    // Complex search methods
    @Query("SELECT r FROM Reservation r " +
           "WHERE r.status = :status " +
           "AND r.dateTime BETWEEN :start AND :end " +
           "AND (r.departureAddress.ville = :city OR r.arrivalAddress.ville = :city)")
    Page<Reservation> findByStatusAndDateTimeAndCity(
        @Param("status") ReservationStatus status,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end,
        @Param("city") String city,
        Pageable pageable
    );
    
    // Statistics methods
    @Query("SELECT COUNT(r) FROM Reservation r " +
           "WHERE r.status = :status " +
           "AND r.dateTime BETWEEN :start AND :end")
    Long countByStatusAndDateRange(
        @Param("status") ReservationStatus status,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
    
    @Query("SELECT r.departureAddress.ville as city, " +
           "COUNT(r) as count, " +
           "AVG(r.price) as avgPrice " +
           "FROM Reservation r " +
           "WHERE r.dateTime BETWEEN :start AND :end " +
           "GROUP BY r.departureAddress.ville " +
           "HAVING COUNT(r) >= :minCount " +
           "ORDER BY count DESC")
    List<Object[]> findPopularDepartureCities(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end,
        @Param("minCount") Long minCount
    );
    
}
