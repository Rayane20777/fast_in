package com.fast_in.repository;

import com.fast_in.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface VehicleRepository  extends JpaRepository<Vehicle, UUID> {
//        @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.vehicle.id = :vehiculeId AND r.dateHeure = :dateTime")
//    boolean hasConflictingReservation(@Param("vehicleId") UUID vehicleId, @Param("dateTime") LocalDateTime dateTime);
}
