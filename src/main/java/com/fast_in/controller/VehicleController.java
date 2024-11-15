package com.fast_in.controller;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fast_in.dto.request.VehicleRequest;
import com.fast_in.dto.response.VehicleResponse;
import com.fast_in.service.VehicleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @Operation(summary = "Get all vehicles", description = "Retrieve a list of all vehicles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of vehicles retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public List<VehicleResponse> getAllVehicles() {
        return vehicleService.findAll();
    }

    @Operation(summary = "Get vehicle by ID", description = "Retrieve a vehicle by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vehicle retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Vehicle not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getById(@Parameter(description = "ID of the vehicle to be retrieved", required = true) @PathVariable UUID id) {
        return vehicleService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle Not Found!"));
    }

    @Operation(summary = "Create a new vehicle", description = "Add a new vehicle to the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Vehicle created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public VehicleResponse create(@Valid @RequestBody VehicleRequest vehicle) {
        return vehicleService.save(vehicle);
    }

    @Operation(summary = "Delete a vehicle", description = "Remove a vehicle from the system by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Vehicle deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Vehicle not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public void delete(@Parameter(description = "ID of the vehicle to be deleted", required = true) @PathVariable UUID id) {
        vehicleService.deleteById(id);
    }

    @Operation(summary = "Update a vehicle", description = "Update an existing vehicle by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vehicle updated successfully"),
        @ApiResponse(responseCode = "404", description = "Vehicle not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public VehicleResponse update(@Parameter(description = "ID of the vehicle to be updated", required = true) @PathVariable UUID id, @Valid @RequestBody VehicleRequest vehicleRequest) {
        return vehicleService.update(id, vehicleRequest);
    }
}
