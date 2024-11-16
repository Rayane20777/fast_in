package com.fast_in.repository;

import com.fast_in.model.Driver;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.UUID;

@Repository
public interface DriverRepository extends JpaRepository<Driver, UUID> {
    
    @Query("SELECT COUNT(d) * 1.0 / (SELECT COUNT(*) FROM Driver) FROM Driver d WHERE d.status = 'EN_COURSE'")
    Double calculateOccupationRate();
    
    @Query("SELECT d.status as status, COUNT(d) as count FROM Driver d GROUP BY d.status")
    Map<String, Integer> getStatusDistribution();
    
    @Query("SELECT FUNCTION('HOUR', d.availabilityStart) as hour, COUNT(d) as count FROM Driver d GROUP BY FUNCTION('HOUR', d.availabilityStart)")
    Map<String, Integer> getAvailabilityDistribution();
}
