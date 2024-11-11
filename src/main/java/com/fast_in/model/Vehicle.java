package com.fast_in.model;

import com.fast_in.model.enums.VehicleStatus;
import com.fast_in.model.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Vehicle {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String registrationNumber;

    @Column(nullable = false)
    private double mileage;

    @Enumerated(EnumType.STRING)
    private VehicleStatus status;

    @Enumerated(EnumType.STRING)
    private VehicleType type;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations;

}
