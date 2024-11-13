package com.fast_in.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.fast_in.dto.request.ReservationRequest;
import com.fast_in.dto.response.ReservationResponse;
import com.fast_in.model.Reservation;

@Mapper(componentModel = "spring", 
        uses = {DriverMapper.class, VehicleMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReservationMapper {



    @Mapping(target = "id", ignore = true)
    @Mapping(target = "prix", ignore = true)
    @Mapping(target = "statut", constant = "CRÉÉE")
    @Mapping(target = "chauffeur.id", source = "driverId")
    @Mapping(target = "vehicule.id", source = "vehiculeId")
    Reservation toEntity(ReservationRequest request);

    ReservationResponse toResponse(Reservation reservation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromRequest(ReservationRequest request, @MappingTarget Reservation reservation);

    default void updatePrix(Reservation reservation, double baseRate, double perKmRate) {
        double prix = baseRate + (perKmRate * reservation.getDistanceKm());
        reservation.setPrix(prix);
    }
}
