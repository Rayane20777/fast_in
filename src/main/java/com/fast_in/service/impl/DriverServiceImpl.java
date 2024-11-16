package com.fast_in.service.impl;

import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fast_in.dto.request.DriverRequest;
import com.fast_in.dto.response.DriverAnalytics;
import com.fast_in.dto.response.DriverResponse;
import com.fast_in.exception.ResourceNotFoundException;
import com.fast_in.mapper.DriverMapper;
import com.fast_in.model.Driver;
import com.fast_in.repository.DriverRepository;
import com.fast_in.service.DriverService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DriverServiceImpl implements DriverService {
    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;

    @Override
    public Page<DriverResponse> findAll(Pageable pageable) {
        return driverRepository.findAll(pageable)
                .map(driverMapper::toResponse);
    }

    @Override
    public Optional<DriverResponse> findById(UUID id) {
        return driverRepository.findById(id)
                .map(driverMapper::toResponse);
    }

    @Override
    public DriverResponse create(DriverRequest request) {
        Driver driver = driverMapper.toEntity(request);
        return driverMapper.toResponse(driverRepository.save(driver));
    }

    @Override
    public DriverResponse update(UUID id, DriverRequest request) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
        
        Driver updatedDriver = driverMapper.toEntity(request);
        updatedDriver.setId(driver.getId());
        
        return driverMapper.toResponse(driverRepository.save(updatedDriver));
    }

    @Override
    public void deleteById(UUID id) {
        if (!driverRepository.existsById(id)) {
            throw new ResourceNotFoundException("Driver not found with id: " + id);
        }
        driverRepository.deleteById(id);
    }

    @Override
    public DriverAnalytics getAnalytics() {
        return DriverAnalytics.builder()
            .occupationRate(calculateOccupationRate())
            .statusDistribution(getStatusDistribution())
            .availabilityDistribution(getAvailabilityDistribution())
            .build();
    }

    private Double calculateOccupationRate() {
        return driverRepository.calculateOccupationRate();
    }

    private Map<String, Integer> getStatusDistribution() {
        return driverRepository.getStatusDistribution();
    }

    private Map<String, Integer> getAvailabilityDistribution() {
        return driverRepository.getAvailabilityDistribution();
    }
} 