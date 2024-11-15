package com.fast_in.service.impl;

import com.fast_in.dao.DriverDao;
import com.fast_in.dto.request.DriverRequest;
import com.fast_in.dto.response.DriverResponse;
import com.fast_in.exception.ResourceNotFoundException;
import com.fast_in.mapper.DriverMapper;
import com.fast_in.model.Driver;
import com.fast_in.repository.DriverRepository;
import com.fast_in.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DriverServiceImpl implements DriverService {
    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;
    private final DriverDao driverDao;

    @Override
    public Page<DriverResponse> findAll(Pageable pageable) {
        return driverRepository.findAll(pageable)
                .map(driverMapper::toResponse);
    }

    @Override
    public Optional<DriverResponse> findById(Long id) {
        return driverRepository.findById(id)
                .map(driverMapper::toResponse);
    }

    @Override
    public DriverResponse create(DriverRequest request) {
        Driver driver = driverMapper.toEntity(request);
        return driverMapper.toResponse(driverRepository.save(driver));
    }

    @Override
    public DriverResponse update(Long id, DriverRequest request) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
        
        Driver updatedDriver = driverMapper.toEntity(request);
        updatedDriver.setId(driver.getId());
        
        return driverMapper.toResponse(driverRepository.save(updatedDriver));
    }

    @Override
    public void deleteById(Long id) {
        if (!driverRepository.existsById(id)) {
            throw new ResourceNotFoundException("Driver not found with id: " + id);
        }
        driverRepository.deleteById(id);
    }

    @Override
    public Map<String, Object> getDriverAnalytics() {
        return driverDao.getDriverAnalytics();
    }
} 