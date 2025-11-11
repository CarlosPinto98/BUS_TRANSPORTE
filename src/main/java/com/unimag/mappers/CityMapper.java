package com.unimag.mappers;

import com.unimag.DTO.CityDTO;
import com.unimag.entities.City;
import com.unimag.entities.Stop;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface CityMapper {

    City toEntity(CityDTO.cityCreateRequest dto);
    CityDTO.cityResponse toResponse(City city);
    Set<CityDTO.stopDTO> toStop(Set<Stop> stops);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(CityDTO.cityUpdateRequest updateRequest, @MappingTarget City city);
}
