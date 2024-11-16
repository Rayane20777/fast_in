package com.fast_in.dto.response;

import com.fast_in.model.enums.DriverStatus;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;
@Data
public class DriverResponse {
    private UUID id;
    private String lastName;
    private String firstName;
    private DriverStatus status;
    private LocalDate availabilityStart;
    private LocalDate availabilityEnd;
}
