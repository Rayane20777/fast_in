package com.fast_in.dao;

import com.fast_in.model.Driver;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverDao extends JpaRepository<Driver, Long> {

    @Query("SELECT new map(" +
           "CASE " +
           "    WHEN COUNT(d) = 0 THEN 'Aucun chauffeur trouvé' " +
           "    ELSE CAST(COALESCE((COUNT(CASE WHEN d.status = 'EN_COURSE' THEN 1 END) * 100.0) / " +
           "         NULLIF(COUNT(d), 0), 0.0) AS string) " +
           "END as occupationRate, " +
           "CASE " +
           "    WHEN MIN(d.availabilityStart) IS NULL THEN 'Aucune date de début disponible' " +
           "    ELSE CAST(MIN(d.availabilityStart) AS string) " +
           "END as earliestStart, " +
           "CASE " +
           "    WHEN MAX(d.availabilityEnd) IS NULL THEN 'Aucune date de fin disponible' " +
           "    ELSE CAST(MAX(d.availabilityEnd) AS string) " +
           "END as latestEnd, " +
           "CASE " +
           "    WHEN MIN(d.availabilityStart) IS NULL OR MAX(d.availabilityEnd) IS NULL THEN 'Impossible de calculer la durée' " +
           "    ELSE CAST(FUNCTION('DATEDIFF', 'DAY', MIN(d.availabilityStart), MAX(d.availabilityEnd)) AS string) " +
           "END as averageDays" +
           ") FROM Driver d")
    Map<String, Object> getDriverAnalytics();
}