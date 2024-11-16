package com.fast_in.dto.response;

import com.fast_in.model.enums.VehicleStatus;
import com.fast_in.model.enums.VehicleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing vehicle details")
public class VehicleResponse {
    private UUID id;
    private String model;
    private String registrationNumber;
    private double mileage;
    private VehicleStatus status;
    private VehicleType type;

}
