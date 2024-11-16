package com.fast_in.mapper;

import org.springframework.stereotype.Component;
import com.fast_in.dto.request.VehicleRequest;
import com.fast_in.dto.response.VehicleResponse;
import com.fast_in.model.Vehicle;

@Component
public class VehicleMapper {
    
    public VehicleResponse toResponse(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }
        
        VehicleResponse response = new VehicleResponse();
        response.setId(vehicle.getId());
        response.setModel(vehicle.getModel());
        response.setType(vehicle.getType());
        response.setStatus(vehicle.getStatus());
        return response;
    }
    
    public Vehicle toEntity(VehicleRequest request) {
        if (request == null) {
            return null;
        }
        
        Vehicle vehicle = new Vehicle();
        vehicle.setModel(request.getModel());
        vehicle.setType(request.getType());
        vehicle.setStatus(request.getStatus());
        return vehicle;
    }
}