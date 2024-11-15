package com.fast_in.dto.request;

import com.fast_in.model.enums.VehicleStatus;
import com.fast_in.model.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
