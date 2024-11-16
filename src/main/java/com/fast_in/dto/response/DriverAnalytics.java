package com.fast_in.dto.response;

import java.util.Map;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Analytics data for drivers")
public class DriverAnalytics {
    
    @Schema(description = "Occupation rate of drivers (percentage of time in EN_COURSE status)")
    private Double occupationRate;
    
    @Schema(description = "Analysis of availability time slots")
    private Map<String, Integer> availabilityDistribution;
    
    @Schema(description = "Distribution of driver statuses by period")
    private Map<String, Integer> statusDistribution;
}