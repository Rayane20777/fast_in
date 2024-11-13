package com.fast_in.model;

import java.time.LocalDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import com.fast_in.model.enums.ReservationStatus;
// import com.fast_in.model.enums.DriverStatus;
import com.fast_in.model.enums.VehicleStatus;

@Entity
@Table(name = "reservation")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_heure", nullable = false)
    @NotNull(message = "Reservation date time is required")
    @FutureOrPresent(message = "Reservation date must be in present or future")
    private LocalDateTime dateTime;

    @Column(name = "course_start_time")
    private LocalDateTime courseStartTime;

    @Column(name = "course_end_time")
    private LocalDateTime courseEndTime;

    @Embedded
    @NotNull(message = "Departure address is required")
    @AttributeOverrides({
        @AttributeOverride(name = "ville", column = @Column(name = "ville_depart", nullable = false)),
        @AttributeOverride(name = "quartier", column = @Column(name = "quartier_depart", nullable = false))
    })
    private Address departureAddress;

    @Embedded
    @NotNull(message = "Arrival address is required")
    @AttributeOverrides({
        @AttributeOverride(name = "ville", column = @Column(name = "ville_arrivee", nullable = false)),
        @AttributeOverride(name = "quartier", column = @Column(name = "quartier_arrivee", nullable = false))
    })
    private Address arrivalAddress;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price must be positive")
    @Column(nullable = false)
    private Double price;

    @NotNull(message = "Distance is required")
    @DecimalMin(value = "1.0", message = "Distance must be at least 1 km")
    @DecimalMax(value = "100.0", message = "Distance cannot exceed 100 km")
    @Column(name = "distance_km", nullable = false)
    private Double distanceKm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    @NotNull(message = "Driver is required")
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    @NotNull(message = "Vehicle is required")
    private Vehicle vehicle;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Business logic methods
    public void confirm() {
        validateStatusTransition(ReservationStatus.CREATED, ReservationStatus.CONFIRMED);
        this.status = ReservationStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
    }

    public void start() {
        validateStatusTransition(ReservationStatus.CONFIRMED, ReservationStatus.CONFIRMED);
        this.status = ReservationStatus.CONFIRMED;
        this.courseStartTime = LocalDateTime.now();
        // this.driver.setStatus(DriverStatus.ONGOING);
        this.vehicle.setStatus(VehicleStatus.ONGOING);
    }

    public void complete() {
        validateStatusTransition(ReservationStatus.CONFIRMED, ReservationStatus.COMPLETED);
        this.status = ReservationStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.courseEndTime = LocalDateTime.now();
        // this.driver.setStatus(DriverStatus.AVAILABLE);
        this.vehicle.setStatus(VehicleStatus.AVAILABLE);
    }

    public void cancel() {
        if (status == ReservationStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a completed reservation");
        }
        this.status = ReservationStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        if (status == ReservationStatus.CONFIRMED) {
            // this.driver.setStatus(DriverStatus.AVAILABLE);
            this.vehicle.setStatus(VehicleStatus.AVAILABLE);
        }
    }

    private void validateStatusTransition(ReservationStatus expectedStatus, ReservationStatus newStatus) {
        if (this.status != expectedStatus) {
            throw new IllegalStateException(
                String.format("Invalid status transition. Expected: %s, Current: %s, New: %s", 
                    expectedStatus, this.status, newStatus)
            );
        }
    }
}