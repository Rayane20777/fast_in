package com.fast_in.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.fast_in.dto.request.DriverRequest;
import com.fast_in.dto.response.DriverResponse;
import com.fast_in.model.Driver;

@Mapper(componentModel = "spring")
public interface DriverMapper {
    DriverResponse toResponse(Driver driver);
    Driver toEntity(DriverRequest request);
}
