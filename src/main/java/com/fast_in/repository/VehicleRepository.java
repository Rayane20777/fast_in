package com.fast_in.repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fast_in.model.Vehicle;

@Repository
public interface VehicleRepository  extends JpaRepository<Vehicle, UUID> {
//        @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.vehicle.id = :vehiculeId AND r.dateHeure = :dateTime")
//    boolean hasConflictingReservation(@Param("vehicleId") UUID vehicleId, @Param("dateTime") LocalDateTime dateTime);

    @Query("SELECT AVG(v.mileage) FROM Vehicle v WHERE v.type = :type")
    Double getAverageMileageByType(@Param("type") String type);

    @Query("SELECT v.type, COUNT(r) * 1.0 / COUNT(v) FROM Vehicle v LEFT JOIN Reservation r ON v.id = r.vehicle.id WHERE r.status = 'OONGOING' GROUP BY v.type")
    Map<String, Double> getUtilizationRateByType();

    @Query("SELECT v.status, COUNT(v) FROM Vehicle v GROUP BY v.status")
    Map<String, Integer> getFleetStatusCount();

    @Query("SELECT v FROM Vehicle v WHERE v.type = :type")
    List<Vehicle> getVehiclesByType(@Param("type") String type);

}
