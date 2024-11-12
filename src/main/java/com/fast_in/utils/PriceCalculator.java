package com.fast_in.utils;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

@Component
public class PriceCalculator {
    
    /**
     * Calculates the base price for a reservation
     * @param distanceKm Distance in kilometers
     * @param baseRate Base rate for the reservation
     * @param perKmRate Rate per kilometer
     * @return Calculated price
     */
    public double calculateBasePrice(double distanceKm, double baseRate, double perKmRate) {
        return baseRate + (distanceKm * perKmRate);
    }

    /**
     * Applies peak hour surcharge if applicable
     * @param basePrice Base price before surcharge
     * @param dateTime Reservation date and time
     * @return Final price after surcharge
     */
    public double applyPeakHourSurcharge(double basePrice, LocalDateTime dateTime) {
        int hour = dateTime.getHour();
        // Peak hours: 7-9 AM and 5-7 PM
        if ((hour >= 7 && hour <= 9) || (hour >= 17 && hour <= 19)) {
            return basePrice * 1.2; // 20% surcharge
        }
        return basePrice;
    }

    /**
     * Applies weekend surcharge if applicable
     * @param price Price before weekend surcharge
     * @param dateTime Reservation date and time
     * @return Final price after weekend surcharge
     */
    public double applyWeekendSurcharge(double price, LocalDateTime dateTime) {
        switch (dateTime.getDayOfWeek()) {
            case SATURDAY:
                return price * 1.1; // 10% surcharge
            case SUNDAY:
                return price * 1.15; // 15% surcharge
            default:
                return price;
        }
    }

    /**
     * Calculates final price including all applicable surcharges
     * @param distanceKm Distance in kilometers
     * @param baseRate Base rate for the reservation
     * @param perKmRate Rate per kilometer
     * @param dateTime Reservation date and time
     * @return Final calculated price
     */
    public double calculateFinalPrice(double distanceKm, double baseRate, double perKmRate, LocalDateTime dateTime) {
        double basePrice = calculateBasePrice(distanceKm, baseRate, perKmRate);
        double priceWithPeakHour = applyPeakHourSurcharge(basePrice, dateTime);
        return applyWeekendSurcharge(priceWithPeakHour, dateTime);
    }
}
