package com.fast_in.dto.request;

import com.fast_in.model.enums.DriverStatut;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class DriverRequest {
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotNull(message = "Driver status is required")
    private DriverStatut status;
    
    @NotNull(message = "Availability start is required")
    private LocalDate availabilityStart;
    
    @NotNull(message = "Availability end is required")
    private LocalDate availabilityEnd;
}
