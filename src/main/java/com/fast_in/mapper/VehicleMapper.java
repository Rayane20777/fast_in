package com.fast_in.mapper;

import org.springframework.stereotype.Component;

import com.fast_in.dto.request.VehicleRequest;
import com.fast_in.dto.response.VehicleResponse;
import com.fast_in.model.Vehicle;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class VehicleMapper {

    public Vehicle toEntity(VehicleRequest request) {
        if (request == null) {
            return null;
        }
        return Vehicle.builder()
                .model(request.getModel())
                .registrationNumber(request.getRegistrationNumber())
                .mileage(request.getMileage())
                .status(request.getStatus())
                .type(request.getType())
                .build();
    }

    public VehicleResponse toResponse(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .model(vehicle.getModel())
                .registrationNumber(vehicle.getRegistrationNumber())
                .mileage(vehicle.getMileage())
                .status(vehicle.getStatus())
                .type(vehicle.getType())
                .build();
    }

    public void updateEntityFromRequest(VehicleRequest request, Vehicle vehicle) {
        if (request == null || vehicle == null) {
            return;
        }
        vehicle.setModel(request.getModel());
        vehicle.setRegistrationNumber(request.getRegistrationNumber());
        vehicle.setMileage(request.getMileage());
        vehicle.setStatus(request.getStatus());
        vehicle.setType(request.getType());
    }
}