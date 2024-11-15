package com.fast_in.controller;

import com.fast_in.dto.request.VehicleRequest;
import com.fast_in.dto.response.VehicleResponse;
import com.fast_in.model.Vehicle;
import com.fast_in.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.fast_in.model.enums.VehicleType.*;
import static com.fast_in.model.enums.VehicleStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VehicleControllerTest {

    @InjectMocks
    private VehicleController vehicleController;

    @Mock
    private VehicleService vehicleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllVehicles() {
        VehicleResponse vehicleResponse = new VehicleResponse(UUID.randomUUID(), "Model X", "ABC123", 10000, null, null);
        when(vehicleService.findAll()).thenReturn(Collections.singletonList(vehicleResponse));

        List<VehicleResponse> response = vehicleController.getAllVehicles();

        assertEquals(1, response.size());
        assertEquals(vehicleResponse, response.get(0));
        verify(vehicleService, times(1)).findAll();
    }

    @Test
    void getById_VehicleFound() {
        UUID vehicleId = UUID.randomUUID();
        VehicleResponse vehicleResponse = new VehicleResponse(vehicleId, "Model X", "ABC123", 10000, null, null);
        when(vehicleService.findById(vehicleId)).thenReturn(Optional.of(vehicleResponse));

        ResponseEntity<VehicleResponse> response = vehicleController.getById(vehicleId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(vehicleResponse, response.getBody());
        verify(vehicleService, times(1)).findById(vehicleId);
    }

    @Test
    void getById_VehicleNotFound() {
        UUID vehicleId = UUID.randomUUID();
        when(vehicleService.findById(vehicleId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResponseStatusException.class, () -> vehicleController.getById(vehicleId));

        assertEquals("404 NOT_FOUND \"Vehicle Not Found!\"", exception.getMessage());
        verify(vehicleService, times(1)).findById(vehicleId);
    }

    @Test
    void createVehicle() {
        VehicleRequest vehicleRequest = new VehicleRequest("Model X", "ABC123", 10000, AVAILABLE, VAN);
        VehicleResponse vehicleResponse = new VehicleResponse(UUID.randomUUID(), "Model X", "ABC123", 10000, null, null);
        when(vehicleService.save(any(VehicleRequest.class))).thenReturn(vehicleResponse);

        VehicleResponse response = vehicleController.create(vehicleRequest);

        assertEquals(vehicleResponse, response);
        verify(vehicleService, times(1)).save(vehicleRequest);
    }

    @Test
    void deleteVehicle() {
        UUID vehicleId = UUID.randomUUID();
        doNothing().when(vehicleService).deleteById(vehicleId);

        vehicleController.delete(vehicleId);

        verify(vehicleService, times(1)).deleteById(vehicleId);
    }

    @Test
    void updateVehicle() {
        UUID vehicleId = UUID.randomUUID();
        VehicleRequest vehicleRequest = new VehicleRequest("Model Y", "XYZ789", 15000, AVAILABLE, SEDAN);
        VehicleResponse existingVehicleResponse = new VehicleResponse(vehicleId, "Model X", "ABC123", 10000, AVAILABLE, VAN);
        VehicleResponse updatedVehicleResponse = new VehicleResponse(vehicleId, "Model Y", "XYZ789", 15000, AVAILABLE, SEDAN);

        when(vehicleService.findById(vehicleId)).thenReturn(Optional.of(existingVehicleResponse));
        when(vehicleService.update(eq(vehicleId), any(VehicleRequest.class))).thenReturn(updatedVehicleResponse);

        VehicleResponse response = vehicleController.update(vehicleId, vehicleRequest);

        assertEquals(updatedVehicleResponse, response);
        verify(vehicleService, times(1)).update(vehicleId, vehicleRequest);
    }
}