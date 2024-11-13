package com.fast_in.service;

import com.fast_in.dto.request.DriverRequest;
import com.fast_in.dto.response.DriverResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface DriverService {
    Page<DriverResponse> findAll(Pageable pageable);
    Optional<DriverResponse> findById(Long id);
    DriverResponse create(DriverRequest request);
    DriverResponse update(Long id, DriverRequest request);
    void deleteById(Long id);
}
