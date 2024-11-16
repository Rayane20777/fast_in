package com.fast_in.model;

import com.fast_in.model.enums.VehicleStatus;
import com.fast_in.model.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Vehicle {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    @Pattern(regexp = "^\\d{1,6}(-[A-Z]-\\d{1,2})?$", message = "Invalid registration number format. Format should be: 123456-A-12 or 123456")
    private String registrationNumber;

    @Column(nullable = false)
    private double mileage;

    @Enumerated(EnumType.STRING)
    private VehicleStatus status;

    @Enumerated(EnumType.STRING)
    private VehicleType type;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL)
    private List<Reservation> reservations;
}
