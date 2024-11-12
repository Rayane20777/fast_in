package com.fast_in.dto.response;

import java.time.LocalDateTime;

import com.fast_in.dto.common.AdresseDto;
import com.fast_in.model.enums.StatutReservation;

import lombok.Data;
// import com.fast_in.dto.common.DriverDto;
// import com.fast_in.dto.common.VehicleDto;

@Data
public class ReservationResponse {
    private Long id;
    private LocalDateTime dateHeure;
    private AdresseDto adresseDepart;
    private AdresseDto adresseArrivee;
    private Double prix;
    private Double distanceKm;
    private StatutReservation statut;
    // private DriverDto chauffeur;
    // private VehicleDto vehicule;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
