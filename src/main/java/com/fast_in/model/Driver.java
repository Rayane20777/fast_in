package com.fast_in.model;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.*;

import com.fast_in.model.enums.DriverStatus;

import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotNull(message = "Driver status is required")
    @Enumerated(EnumType.STRING)
    private DriverStatus status;

    @NotNull(message = "Availability start is required")
    private LocalDate availabilityStart;
    
    @NotNull(message = "Availability end is required")
    private LocalDate availabilityEnd;

    @OneToMany(mappedBy = "driver")
    private List<Reservation> reservations;
    
    @PrePersist
    @PreUpdate
    private void validateBusinessRules() {
        if (availabilityEnd.isBefore(availabilityStart)) {
            throw new IllegalStateException("Availability end date must be after start date");
        }
    }
}
