// package com.fast_in.service.impl;

// import com.fast_in.exception.ResourceNotFoundException;
// import com.fast_in.repository.DriverRepository;
// import com.fast_in.service.DriverService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.time.LocalDateTime;
// import java.util.UUID;

// @Service
// @RequiredArgsConstructor
// @Transactional
// public class DriverServiceImpl implements DriverService {

//     private final DriverRepository driverRepository;


//     // @Override
//     // public boolean isAvailable(UUID driverId, LocalDateTime dateTime) {
//     //     if (!driverRepository.existsById(driverId)) {
//     //         throw new ResourceNotFoundException("Driver not found with id: " + driverId);
//     //     }
//     //     return !driverRepository.hasConflictingReservation(driverId, dateTime);
//     // }
// }