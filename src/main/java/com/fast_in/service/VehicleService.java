package com.fast_in.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.fast_in.dto.request.VehicleRequest;
import com.fast_in.dto.response.VehicleResponse;

public interface VehicleService {
    List<VehicleResponse> findAll();
    Optional<VehicleResponse> findById(UUID id);
    VehicleResponse save(VehicleRequest vehicleRequest);
    void deleteById(UUID id);
    boolean isAvailable(Long vehicleId, LocalDateTime dateTime);
}