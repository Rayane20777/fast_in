package com.fast_in.repository;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fast_in.model.Vehicle;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    @Query("SELECT COUNT(r) > 0 FROM Reservation r " +
           "WHERE r.vehicule.id = :vehiculeId " +
           "AND r.statut IN ('CRÉÉE', 'CONFIRMÉE') " +
           "AND r.dateHeure = :dateTime")
    boolean hasConflictingReservations(@Param("vehiculeId") UUID vehiculeId, 
                                     @Param("dateTime") LocalDateTime dateTime);
}
