package com.fast_in.service.impl;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.fast_in.dto.request.VehicleRequest;
import com.fast_in.dto.response.VehicleAnalytics;
import com.fast_in.dto.response.VehicleResponse;
import com.fast_in.exception.ResourceNotFoundException;
import com.fast_in.mapper.VehicleMapper;
import com.fast_in.model.Vehicle;
import com.fast_in.repository.VehicleRepository;
import com.fast_in.service.VehicleService;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleServiceImpl implements VehicleService {
    
    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    @Override
    public List<VehicleResponse> findAll() {
        return vehicleRepository.findAll().stream()
            .map(vehicleMapper::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<VehicleResponse> findById(UUID id) {
        return vehicleRepository.findById(id)
            .map(vehicleMapper::toResponse);
    }

    @Override
    @Transactional
    public VehicleResponse save(VehicleRequest request) {
        Vehicle vehicle = vehicleMapper.toEntity(request);
        return vehicleMapper.toResponse(vehicleRepository.save(vehicle));
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        if (!vehicleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vehicle not found with id: " + id);
        }
        vehicleRepository.deleteById(id);
    }

    @Override
    @Transactional
    public VehicleResponse update(UUID id, VehicleRequest request) {
        Vehicle vehicle = vehicleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));
        
        Vehicle updatedVehicle = vehicleMapper.toEntity(request);
        updatedVehicle.setId(vehicle.getId());
        
        return vehicleMapper.toResponse(vehicleRepository.save(updatedVehicle));
    }

    @Override
    public boolean isAvailable(UUID vehicleId, LocalDateTime dateTime) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new ResourceNotFoundException("Vehicle not found with id: " + vehicleId);
        }
        return !vehicleRepository.hasVehicleConflictingReservation(vehicleId, dateTime);
    }

    @Override
    public VehicleAnalytics getVehicleAnalytics() {
        log.info("Generating vehicle analytics");
        
        Map<String, Double> averageMileageByType = convertToDoubleMap(
            vehicleRepository.getAverageMileageByType()
        );
        
        Map<String, Double> utilizationRateByType = convertToDoubleMap(
            vehicleRepository.getUtilizationRateByType()
        );
        
        Map<String, Integer> fleetStatusCount = convertToIntegerMap(
            vehicleRepository.getFleetStatusCount()
        );
        
        Map<String, Double> revenueByType = convertToDoubleMap(
            vehicleRepository.getTotalRevenueByType()
        );
        
        Map<String, Integer> tripsByType = convertToIntegerMap(
            vehicleRepository.getTotalTripsByType()
        );
        
        return VehicleAnalytics.builder()
            .averageMileageByType(averageMileageByType)
            .utilizationRateByType(utilizationRateByType)
            .fleetStatusCount(fleetStatusCount)
            .revenueByType(revenueByType)
            .tripsByType(tripsByType)
            .build();
    }

    private Map<String, Double> convertToDoubleMap(List<Object[]> results) {
        return results.stream()
            .filter(row -> row[0] != null && row[1] != null)
            .collect(Collectors.toMap(
                row -> row[0].toString(),
                row -> ((Number) row[1]).doubleValue(),
                (a, b) -> a,
                LinkedHashMap::new
            ));
    }

    private Map<String, Integer> convertToIntegerMap(List<Object[]> results) {
        return results.stream()
            .filter(row -> row[0] != null && row[1] != null)
            .collect(Collectors.toMap(
                row -> row[0].toString(),
                row -> ((Number) row[1]).intValue(),
                (a, b) -> a,
                LinkedHashMap::new
            ));
    }
}