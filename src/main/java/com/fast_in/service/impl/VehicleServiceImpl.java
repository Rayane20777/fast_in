package com.fast_in.service.impl;

import com.fast_in.dto.request.VehicleRequest;
import com.fast_in.dto.response.VehicleResponse;
import com.fast_in.exception.ResourceNotFoundException;
import com.fast_in.mapper.VehicleMapper;
import com.fast_in.model.Vehicle;
import com.fast_in.repository.VehicleRepository;
import com.fast_in.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
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
        return vehicleRepository.findById(id).map(vehicleMapper::toResponse);
    }

    @Override
    public VehicleResponse save(VehicleRequest vehicleRequest) {
        Vehicle vehicle = vehicleMapper.toEntity(vehicleRequest);
        return vehicleMapper.toResponse(vehicleRepository.save(vehicle));
    }

    @Override
    public void deleteById(UUID id) {
        vehicleRepository.deleteById(id);
    }

    @Override
    public VehicleResponse update(UUID id, VehicleRequest vehicleRequest) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle Not Found!"));

        // Update the vehicle fields using the mapper
        vehicleMapper.updateEntityFromRequest(vehicleRequest, vehicle);
        
        // Save the updated vehicle
        return vehicleMapper.toResponse(vehicleRepository.save(vehicle));
    }


    @Override
    public boolean isAvailable(UUID vehicleId, LocalDateTime dateTime) {
//        // Check if vehicle exists
//        if (!vehicleRepository.existsById(vehicleId)) {
//            throw new ResourceNotFoundException("Vehicle not found with id: " + vehicleId);
//        }
//
//        // Check vehicle's availability
//        return !vehicleRepository.hasConflictingReservation(vehicleId, dateTime);
        return true;
    }
}