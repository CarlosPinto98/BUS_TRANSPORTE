package com.unimag.mappers;

import com.unimag.DTO.FareRuleDTO.*;
import com.unimag.entities.FareRule;
import com.unimag.entities.Route;
import com.unimag.entities.Stop;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface FareRuleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "basePrice", source = "basePrice")
    @Mapping(target = "discounts", source = "discounts")
    @Mapping(target = "dynamicPricing", source = "dynamicPricing")
    @Mapping(target = "route", source = "routeId", qualifiedByName = "mapRoute")
    @Mapping(target = "fromStop", source = "fromStopId", qualifiedByName = "mapStop")
    @Mapping(target = "toStop", source = "toStopId", qualifiedByName = "mapStop")
    FareRule toEntity(fareRuleCreateRequest dto);


    @Mapping(target = "basePrice", source = "basePrice")
    @Mapping(target = "discounts", source = "discounts")
    @Mapping(target = "dynamicPricing", source = "dynamicPricing")
    void updateEntity(fareRuleUpdateRequest dto, @MappingTarget FareRule entity);

    @Mapping(target = "routeId", source = "route.id")
    @Mapping(target = "fromStopId", source = "fromStop.id")
    @Mapping(target = "toStopId", source = "toStop.id")
    @Mapping(target = "fromStopName", source = "fromStop.name")
    @Mapping(target = "toStopName", source = "toStop.name")
    @Mapping(target = "dynamicPricing", source = "dynamicPricing")
    fareRuleResponse toResponse(FareRule entity);

    @Named("mapRoute")
    default Route mapRoute(Long id) {
        if (id == null) return null;
        Route r = new Route();
        r.setId(id);
        return r;
    }
    @Named("mapStop")
    default Stop mapStop(Long id) {
        if (id == null) return null;
        Stop s = new Stop();
        s.setId(id);
        return s;
    }
}
