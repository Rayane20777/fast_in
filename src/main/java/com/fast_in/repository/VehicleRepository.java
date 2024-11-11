package com.fast_in.repository;

import com.fast_in.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VehicleRepository  extends JpaRepository<Vehicle, UUID> {
}
