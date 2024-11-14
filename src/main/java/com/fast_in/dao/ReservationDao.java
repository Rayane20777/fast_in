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
    // Basic finder methods
    List<Reservation> findByStatus(ReservationStatus status);
    
    @Query("SELECT r FROM Reservation r WHERE r.driver.id = :driverId")
    Page<Reservation> findByDriverId(@Param("driverId") Long driverId, Pageable pageable);
    
    @Query("SELECT r FROM Reservation r WHERE r.vehicle.id = :vehicleId")
    Page<Reservation> findByVehicleId(@Param("vehicleId") UUID vehicleId, Pageable pageable);
    
    // Analytics queries as per requirements
    @Query("SELECT AVG(r.price / r.distanceKm) FROM Reservation r WHERE r.status = 'COMPLETED'")
    Double calculateAveragePricePerKm();
    
    @Query("SELECT AVG(r.distanceKm) FROM Reservation r WHERE r.status = 'COMPLETED'")
    Double calculateAverageDistance();
    
    @Query("SELECT HOUR(r.dateTime) as hour, COUNT(r) as count " +
           "FROM Reservation r " +
           "GROUP BY HOUR(r.dateTime) " +
           "ORDER BY hour")
    List<Object[]> getReservationsByHourDistribution();
    
    @Query("SELECT r.departureAddress.ville, COUNT(r) " +
           "FROM Reservation r " +
           "GROUP BY r.departureAddress.ville " +
           "ORDER BY COUNT(r) DESC")
    List<Object[]> findMostRequestedLocations();
}
