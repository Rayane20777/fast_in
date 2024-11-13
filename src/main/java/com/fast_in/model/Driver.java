package com.fast_in.model;

import lombok.Data;
import javax.persistence.*;

@Entity
@Data
@Table(name = "drivers")
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Add other driver fields here
}
