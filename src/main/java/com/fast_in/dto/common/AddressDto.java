package com.fast_in.dto.common;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class AddressDto {
    @NotBlank(message = "The city is required")
    private String ville;
    
    @NotBlank(message = "The neighborhood is required")
    private String quartier;
} 