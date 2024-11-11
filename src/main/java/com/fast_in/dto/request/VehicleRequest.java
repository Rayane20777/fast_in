package com.fast_in.dto.request;

import com.fast_in.model.enums.VehicleStatus;
import com.fast_in.model.enums.VehicleType;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class VehicleRequest {
    @NotNull
    private String model;

    @NotNull
    private String registrationNumber;

    @NotNull
    private double mileage;

    @NotNull
    private VehicleStatus status;

    @NotNull
    private VehicleType type;
}
