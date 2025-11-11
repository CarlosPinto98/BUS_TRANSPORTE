package com.unimag.mappers;

import com.unimag.DTO.BaggageDTO.*;
import com.unimag.entities.Baggage;
import com.unimag.entities.Ticket;
import com.unimag.entities.Trip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface BaggageMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "weightKg", source = "weightKg")
    @Mapping(target = "tagCode", expression = "java(generateTagCode())")
    @Mapping(target = "ticket", source = "ticketId", qualifiedByName = "mapTicket")
    @Mapping(target = "fee", source = "weightKg", qualifiedByName = "calculateFee")
    Baggage toEntity(baggageCreateRequest dto);

    @Mapping(target = "fee", source = "fee")
    void updateEntity(baggageUpdateRequest dto, @MappingTarget Baggage baggage);


    @Mapping(target = "id", source = "id")
    @Mapping(target = "weightKg", source = "weightKg")
    @Mapping(target = "fee", source = "fee")
    @Mapping(target = "tagCode", source = "tagCode")
    @Mapping(target = "ticketId", source = "ticket.id")
    @Mapping(target = "passengerName", source = "ticket.passenger.name")
    @Mapping(target = "tripInfo", source = "ticket.trip", qualifiedByName = "formatTripInfo")
    @Mapping(target = "excessWeight", ignore = true)
    baggageResponse toResponse(Baggage entity);
    @Named("mapTicket")
    default Ticket mapTicket(Long id) {
        if (id == null) return null;
        Ticket t = new Ticket();
        t.setId(id);
        return t;
    }

    @Named("formatTripInfo")
    default String formatTripInfo(Trip trip) {
        if (trip == null) return null;
        return trip.getRoute().getOrigin() + " → " + trip.getRoute().getDestination() +
                " (" + trip.getDate() + ")";
    }
    default String generateTagCode() {
        return "BAG-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 1000);
    }
    @Named("calculateFee")
    default java.math.BigDecimal calculateFee(java.math.BigDecimal weightKg) {
        java.math.BigDecimal freeWeight = new java.math.BigDecimal("20.0");
        java.math.BigDecimal pricePerKg = new java.math.BigDecimal("2000");

        if (weightKg.compareTo(freeWeight) <= 0) {
            return java.math.BigDecimal.ZERO;
        }

        java.math.BigDecimal excess = weightKg.subtract(freeWeight);
        return excess.multiply(pricePerKg);
    }
}
