package com.fast_in.dto.request;

import java.time.LocalDate;
import java.util.UUID;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fast_in.model.enums.DriverStatus;
import com.fast_in.validation.EnumValidator;

import lombok.Data;

@Data
public class DriverRequest {
    private UUID id;
    
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;
    
    @NotNull(message = "Status is required")
    @EnumValidator(enumClass = DriverStatus.class, message = "Invalid driver status")
    private DriverStatus status;
    
    @NotNull(message = "Availability start date is required")
    @FutureOrPresent(message = "Availability start date must be present or future")
    private LocalDate availabilityStart;
    
    @NotNull(message = "Availability end date is required")
    @Future(message = "Availability end date must be in the future")
    private LocalDate availabilityEnd;
}
