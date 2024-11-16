package com.fast_in.dto.response;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@Schema(description = "Analytics data for reservations")
public class ReservationAnalytics {
    
    @Schema(description = "Average price per kilometer across all reservations")
    private Double averagePricePerKm;
    
    @Schema(description = "Average distance across all reservations")
    private Double averageDistance;
    
    @Schema(description = "Most requested departure locations with their counts")
    private List<LocationCount> mostRequestedLocations;
    
    @Schema(description = "Distribution of reservations by hour")
    private Map<Integer, Integer> hourlyDistribution;
    
    @Schema(description = "Distribution of reservations by time slot")
    private Map<String, Integer> timeSlotDistribution;
    
    @Schema(description = "Average price per kilometer by vehicle type")
    private Map<String, Double> pricePerKmByVehicleType;
    
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationCount {
        @Schema(description = "Location name")
        private String location;
        
        @Schema(description = "Number of reservations for this location")
        private Integer count;
    }
} 