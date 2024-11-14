package com.fast_in.dto.response;

import com.fast_in.model.enums.DriverStatut;
import lombok.Data;
import java.time.LocalDate;

@Data
public class DriverResponse {
    private Long id;
    private String lastName;
    private String firstName;
    private DriverStatut status;
    private LocalDate availabilityStart;
    private LocalDate availabilityEnd;
}
