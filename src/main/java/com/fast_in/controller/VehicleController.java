package com.fast_in.controller;

import com.fast_in.dto.response.VehicleResponse;
import com.fast_in.service.Interface.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {
    @Autowired
    private VehicleService vehicleService;

    @GetMapping
    public List<VehicleResponse> getAllVehicles() {
        return vehicleService.findAll();
    }
}
