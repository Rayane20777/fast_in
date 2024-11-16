package com.fast_in.controller;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fast_in.dto.request.DriverRequest;
import com.fast_in.dto.response.DriverAnalytics;
import com.fast_in.dto.response.DriverResponse;
import com.fast_in.exception.ResourceNotFoundException;
import com.fast_in.service.DriverService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/drivers")
@RequiredArgsConstructor
@Slf4j
public class DriverController {
    private final DriverService driverService;

    @GetMapping
    @Operation(summary = "Get all drivers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Drivers retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<DriverResponse>> getAllDrivers(Pageable pageable) {
        log.info("Fetching all drivers");
        return ResponseEntity.ok(driverService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get driver by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Driver retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Driver not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DriverResponse> getById(@PathVariable UUID id) {
        log.info("Fetching driver with id: {}", id);
        return driverService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
    }

    @PostMapping
    @Operation(summary = "Create a new driver")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Driver created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DriverResponse> create(@Valid @RequestBody DriverRequest request) {
        log.info("Creating new driver");
        return new ResponseEntity<>(driverService.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing driver")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Driver updated successfully"),
        @ApiResponse(responseCode = "404", description = "Driver not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DriverResponse> update(@PathVariable UUID id, @Valid @RequestBody DriverRequest request) {
        log.info("Updating driver with id: {}", id);
        return ResponseEntity.ok(driverService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a driver")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Driver deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Driver not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("Deleting driver with id: {}", id);
        driverService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/analytics")
    @Operation(summary = "Get driver analytics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Analytics retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DriverAnalytics> getAnalytics() {
        log.info("Fetching driver analytics");
        return ResponseEntity.ok(driverService.getAnalytics());
    }
}
