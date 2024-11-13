package com.fast_in.service;

import java.time.LocalDateTime;

public interface DriverService {
    boolean isAvailable(Long driverId, LocalDateTime dateHeure);
}
