package com.fast_in.dto.response;

import com.fast_in.model.enums.VehicleStatus;
import com.fast_in.model.enums.VehicleType;
import lombok.Data;

import java.util.UUID;

@Data
public class VehicleResponse {
    private UUID id;
    private String model;
    private String registrationNumber;
    private double mileage;
    private VehicleStatus status;
    private VehicleType type;

}
