package com.fast_in.controller;

import com.fast_in.dto.request.DriverRequest;
import com.fast_in.dto.response.DriverResponse;
import com.fast_in.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/drivers")
@RequiredArgsConstructor
public class DriverController {
    private final DriverService driverService;

    @GetMapping
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of drivers")
    public Page<DriverResponse> getAllDrivers(Pageable pageable) {
        return driverService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved driver"),
        @ApiResponse(responseCode = "404", description = "Driver not found")
    })
    public ResponseEntity<DriverResponse> getById(@PathVariable Long id) {
        return driverService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found"));
    }

    @PostMapping
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Driver successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public DriverResponse create(@Valid @RequestBody DriverRequest request) {
        return driverService.create(request);
    }

    @PutMapping("/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Driver successfully updated"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Driver not found")
    })
    public DriverResponse update(@PathVariable Long id, @Valid @RequestBody DriverRequest request) {
        return driverService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Driver successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Driver not found")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        driverService.deleteById(id);
    }

    @GetMapping("/analytics")
    public Map<String, Object> getDriverAnalytics() {
        return driverService.getDriverAnalytics();
    }
}
