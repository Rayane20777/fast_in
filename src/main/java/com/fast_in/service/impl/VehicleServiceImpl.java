package com.fast_in.service.impl;

import com.fast_in.dto.request.VehicleRequest;
import com.fast_in.dto.response.VehicleResponse;
import com.fast_in.exception.ResourceNotFoundException;
import com.fast_in.model.Vehicle;
import com.fast_in.repository.VehicleRepository;
import com.fast_in.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VehicleServiceImpl implements VehicleService {
    @Autowired
    private VehicleRepository vehicleRepository;

    @Override
    public boolean isAvailable(UUID vehicleId, LocalDateTime dateTime) {
        // Check if vehicle exists
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new ResourceNotFoundException("Vehicle not found with id: " + vehicleId);
        }

        // Check vehicle's availability
        return !vehicleRepository.hasConflictingReservations(vehicleId, dateTime);
    }

    @Override
    public List<VehicleResponse> findAll() {
        return vehicleRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<VehicleResponse> findById(UUID id) {
        return vehicleRepository.findById(id).map(this::convertToResponse);
    }

    @Override
    public VehicleResponse save(VehicleRequest vehicleRequest) {
        Vehicle vehicle = new Vehicle();
        vehicle.setModel(vehicleRequest.getModel());
        vehicle.setRegistrationNumber(vehicleRequest.getRegistrationNumber());
        vehicle.setMileage(vehicleRequest.getMileage());
        vehicle.setStatus(vehicleRequest.getStatus());
        vehicle.setType(vehicleRequest.getType());
        return convertToResponse(vehicleRepository.save(vehicle));
    }

    @Override
    public void deleteById(UUID id) {
        vehicleRepository.deleteById(id);
    }

    private VehicleResponse convertToResponse(Vehicle vehicle) {
        VehicleResponse response = new VehicleResponse();
        response.setId(vehicle.getId());
        response.setModel(vehicle.getModel());
        response.setRegistrationNumber(vehicle.getRegistrationNumber());
        response.setMileage(vehicle.getMileage());
        response.setStatus(vehicle.getStatus());
        response.setType(vehicle.getType());
        return response;
    }
}