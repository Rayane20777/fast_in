package com.fast_in.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fast_in.model.Vehicle;

@Repository
public interface VehicleRepository  extends JpaRepository<Vehicle, UUID> {
//        @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.vehicle.id = :vehiculeId AND r.dateHeure = :dateTime")
//    boolean hasConflictingReservation(@Param("vehicleId") UUID vehicleId, @Param("dateTime") LocalDateTime dateTime);
}
