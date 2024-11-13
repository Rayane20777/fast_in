package com.fast_in.utils;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fast_in.model.enums.VehicleType;

@Component
public class PriceCalculator {

    @Value("${pricing.base-rate:10.0}")
    private double baseRate;

    @Value("${pricing.peak-hours-multiplier:1.5}")
    private double peakHoursMultiplier;

    @Value("${pricing.weekend-multiplier:1.2}")
    private double weekendMultiplier;

    @Value("${pricing.luxury-multiplier:2.0}")
    private double luxuryMultiplier;

    public double calculatePrice(double distanceKm, VehicleType vehicleType, LocalDateTime dateTime) {
        double basePrice = baseRate * distanceKm;
        
        // Apply vehicle type multiplier
        double price = applyVehicleTypeMultiplier(basePrice, vehicleType);
        
        // Apply time-based multipliers
        price = applyTimeBasedMultipliers(price, dateTime);
        
        return Math.round(price * 100.0) / 100.0; // Round to 2 decimal places
    }

    private double applyVehicleTypeMultiplier(double price, VehicleType vehicleType) {
        if (vehicleType == null) {
            return price;
        }
        
        switch (vehicleType) {
            case SEDAN:
                return price;
            case MINIBUS:
                return price * luxuryMultiplier;
            case VAN:
                return price * 1.3;
            default:
                return price;
        }
    }

    private double applyTimeBasedMultipliers(double price, LocalDateTime dateTime) {
        double finalPrice = price;
        
        // Apply peak hours multiplier (7-9 AM and 4-7 PM on weekdays)
        if (isPeakHour(dateTime)) {
            finalPrice *= peakHoursMultiplier;
        }
        
        // Apply weekend multiplier
        if (isWeekend(dateTime)) {
            finalPrice *= weekendMultiplier;
        }
        
        return finalPrice;
    }

    private boolean isPeakHour(LocalDateTime dateTime) {
        if (isWeekend(dateTime)) {
            return false;
        }

        LocalTime time = dateTime.toLocalTime();
        return (time.isAfter(LocalTime.of(6, 59)) && time.isBefore(LocalTime.of(9, 1))) ||
               (time.isAfter(LocalTime.of(15, 59)) && time.isBefore(LocalTime.of(19, 1)));
    }

    private boolean isWeekend(LocalDateTime dateTime) {
        return dateTime.getDayOfWeek().getValue() >= 6;
    }
}
