package com.fast_in.service;

import com.fast_in.dto.request.DriverRequest;
import com.fast_in.dto.response.DriverAnalytics;
import com.fast_in.dto.response.DriverResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;import java.util.UUID;
public interface DriverService {
    Page<DriverResponse> findAll(Pageable pageable);
    Optional<DriverResponse> findById(UUID id);
    DriverResponse create(DriverRequest request);
    DriverResponse update(UUID id, DriverRequest request);
    void deleteById(UUID id);
    DriverAnalytics getAnalytics();
}
