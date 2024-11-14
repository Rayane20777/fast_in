package com.fast_in.model.enums;

/**
 * Represents the possible states of a reservation in the system.
 */
public enum ReservationStatus {
    /**
     * Initial state when reservation is first created
     */
    CREATED,
    
    /**
     * Reservation has been confirmed by the system
     */
    CONFIRMED,
    
    /**
     * Reservation has been completed successfully
     */
    COMPLETED,
    
    /**
     * Reservation has been cancelled
     */
    CANCELLED
}
