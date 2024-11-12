package com.fast_in.dto.common;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class AdresseDto {
    @NotBlank(message = "La ville est obligatoire")
    private String ville;
    
    @NotBlank(message = "Le quartier est obligatoire")
    private String quartier;
} 