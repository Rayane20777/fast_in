package com.fast_in.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fast_in.model.Vehicle;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    @Query("SELECT v.type, AVG(v.mileage) FROM Vehicle v GROUP BY v.type")
    List<Object[]> getAverageMileageByType();

    @Query("SELECT v.type, COUNT(r) * 100.0 / (SELECT COUNT(*) FROM Reservation) "
            + "FROM Vehicle v LEFT JOIN v.reservations r "
            + "WHERE r.status = 'COMPLETED' GROUP BY v.type")
    List<Object[]> getUtilizationRateByType();

    @Query("SELECT v.status, COUNT(v) FROM Vehicle v GROUP BY v.status")
    List<Object[]> getFleetStatusCount();

    @Query("SELECT v.type, SUM(r.price) FROM Vehicle v "
            + "JOIN v.reservations r WHERE r.status = 'COMPLETED' GROUP BY v.type")
    List<Object[]> getTotalRevenueByType();

    @Query("SELECT v.type, COUNT(r) FROM Vehicle v "
            + "JOIN v.reservations r WHERE r.status = 'COMPLETED' GROUP BY v.type")
    List<Object[]> getTotalTripsByType();

    @Query("SELECT COUNT(r) > 0 FROM Reservation r "
            + "WHERE r.vehicle.id = :vehicleId "
            + "AND r.status NOT IN ('COMPLETED', 'CANCELLED') "
            + "AND :dateTime BETWEEN r.dateTime AND r.courseEndTime")
    boolean hasVehicleConflictingReservation(
            @Param("vehicleId") UUID vehicleId,
            @Param("dateTime") LocalDateTime dateTime
    );
}
