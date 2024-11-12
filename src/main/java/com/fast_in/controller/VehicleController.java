package com.fast_in.controller;

import com.fast_in.dto.request.VehicleRequest;
import com.fast_in.dto.response.VehicleResponse;
import com.fast_in.service.Interface.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;



    @GetMapping
    public List<VehicleResponse> getAllVehicles() {
        return vehicleService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getById(@PathVariable UUID id){
        return vehicleService.findById(id).map(ResponseEntity::ok).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle Not Found !"));
    }

    @PostMapping("/{id}")
    public VehicleResponse create(@Valid @RequestBody VehicleRequest vehicle){
        return vehicleService.save(vehicle);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id){
        vehicleService.deleteById(id);
    }



}
