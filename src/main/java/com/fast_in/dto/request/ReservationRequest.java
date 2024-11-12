package com.fast_in.dto.request;

import java.time.LocalDateTime;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;     
import javax.validation.constraints.NotNull;

import com.fast_in.dto.common.AdresseDto;

import lombok.Data;

@Data
public class ReservationRequest {
    @NotNull(message = "La date et l'heure sont obligatoires")
    private LocalDateTime dateHeure;

    @NotNull(message = "L'adresse de départ est obligatoire")
    private AdresseDto adresseDepart;

    @NotNull(message = "L'adresse d'arrivée est obligatoire")
    private AdresseDto adresseArrivee;

    @NotNull(message = "La distance est obligatoire")
    @Min(value = 1, message = "La distance minimale est de 1 km")
    @Max(value = 100, message = "La distance maximale est de 100 km")
    private Double distanceKm;

    @NotNull(message = "L'ID du chauffeur est obligatoire")
    private Long chauffeurId;

    @NotNull(message = "L'ID du véhicule est obligatoire")
    private Long vehiculeId;
}
