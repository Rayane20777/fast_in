package com.fast_in.controller;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import com.fast_in.dto.response.VehicleAnalytics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fast_in.dto.request.VehicleRequest;
import com.fast_in.dto.response.VehicleResponse;
import com.fast_in.exception.ResourceNotFoundException;
import com.fast_in.service.VehicleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
@Slf4j
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    @Operation(summary = "Get all vehicles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vehicles retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<VehicleResponse>> getAllVehicles() {
        log.info("Fetching all vehicles");
        return ResponseEntity.ok(vehicleService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get vehicle by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vehicle retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Vehicle not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<VehicleResponse> getById(@PathVariable UUID id) {
        log.info("Fetching vehicle with id: {}", id);
        return vehicleService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));
    }

    @PostMapping
    @Operation(summary = "Create a new vehicle")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Vehicle created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<VehicleResponse> create(
            @Valid @RequestBody(required = true) VehicleRequest request) {
        log.info("Creating new vehicle: {}", request);
        return new ResponseEntity<>(vehicleService.save(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing vehicle")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vehicle updated successfully"),
        @ApiResponse(responseCode = "404", description = "Vehicle not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<VehicleResponse> update(@PathVariable UUID id, @Valid @RequestBody VehicleRequest request) {
        log.info("Updating vehicle with id: {}", id);
        return ResponseEntity.ok(vehicleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a vehicle")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Vehicle deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Vehicle not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("Deleting vehicle with id: {}", id);
        vehicleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/analytics")
    @Operation(summary = "Get vehicle analytics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Analytics retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<VehicleAnalytics> getAnalytics() {
        log.info("Fetching vehicle analytics");
        VehicleAnalytics analytics = vehicleService.getVehicleAnalytics();
        return ResponseEntity.ok(analytics);
    }
}
