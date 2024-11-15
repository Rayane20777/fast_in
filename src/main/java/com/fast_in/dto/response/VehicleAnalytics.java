package com.fast_in.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class VehicleAnalytics {
    private Map<String, Double> averageMileageByType;
    private Map<String, Double> utilizationRateByType;
    private Map<String, Integer> fleetStatusCount;
}
