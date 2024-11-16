package com.fast_in.dto.request;

import com.fast_in.model.enums.VehicleStatus;
import com.fast_in.model.enums.VehicleType;
import com.fast_in.validation.EnumValidator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRequest {
    @NotBlank(message = "Model is required")
    private String model;

    @NotBlank(message = "Registration number is required")
    @Pattern(regexp = "^\\d{1,6}(-[A-Z]-\\d{1,2})?$", message = "Invalid registration number format")
    private String registrationNumber;

    @NotNull(message = "Mileage is required")
    @Min(value = 0, message = "Mileage cannot be negative")
    private Integer mileage;

    @NotNull(message = "Status is required")
    @EnumValidator(enumClass = VehicleStatus.class, message = "Invalid vehicle status")
    private VehicleStatus status;

    @NotNull(message = "Type is required")
    @EnumValidator(enumClass = VehicleType.class, message = "Invalid vehicle type")
    private VehicleType type;
}