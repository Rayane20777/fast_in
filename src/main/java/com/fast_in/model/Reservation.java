package com.fast_in.model;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fast_in.model.enums.StatutReservation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {  

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime dateHeure;

    @Embedded
    private Adresse adresseDepart;

    @Embedded
    private Adresse adresseArrivee;

    @Column(nullable = false)
    private Double prix;

    @Column(nullable = false)
    private Double distanceKm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutReservation statut;

    @ManyToOne
    @JoinColumn(name = "chauffeur_id", nullable = false)
    private Driver chauffeur;

    @ManyToOne
    @JoinColumn(name = "vehicule_id", nullable = false)
    private Vehicle vehicule;
}
