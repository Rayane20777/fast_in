package com.fast_in.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.fast_in.dto.request.DriverRequest;
import com.fast_in.dto.response.DriverResponse;
import com.fast_in.model.Driver;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DriverMapper {
    public DriverResponse toResponse(Driver driver) {
        if (driver == null) {
            return null;
        }
        
        DriverResponse response = new DriverResponse();
        response.setId(driver.getId());
        response.setLastName(driver.getLastName());
        response.setFirstName(driver.getFirstName());
        response.setStatus(driver.getStatus());
        response.setAvailabilityStart(driver.getAvailabilityStart());
        response.setAvailabilityEnd(driver.getAvailabilityEnd());
        return response;
    }
    
    public Driver toEntity(DriverRequest request) {
        if (request == null) {
            return null;
        }
        
        Driver driver = new Driver();
        driver.setLastName(request.getLastName());
        driver.setFirstName(request.getFirstName());
        driver.setStatus(request.getStatus());
        driver.setAvailabilityStart(request.getAvailabilityStart());
        driver.setAvailabilityEnd(request.getAvailabilityEnd());
        return driver;
    }
}
