package com.fast_in.service.Interface;

import com.fast_in.dto.request.VehicleRequest;
import com.fast_in.dto.response.VehicleResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehicleService {
    List<VehicleResponse> findAll();
    Optional<VehicleResponse> findById(UUID id);
    VehicleResponse save(VehicleRequest vehicleRequest);
    void deleteById(UUID id);
}
