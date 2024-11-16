package com.fast_in.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@Schema(description = "Analytics data for vehicles")
public class VehicleAnalytics {
    @Schema(description = "Average mileage by vehicle type")
    private Map<String, Double> averageMileageByType;
    
    @Schema(description = "Utilization rate by vehicle type (percentage)")
    private Map<String, Double> utilizationRateByType;
    
    @Schema(description = "Count of vehicles by status")
    private Map<String, Integer> fleetStatusCount;
    
    @Schema(description = "Total revenue by vehicle type")
    private Map<String, Double> revenueByType;
    
    @Schema(description = "Total number of trips by vehicle type")
    private Map<String, Integer> tripsByType;
}