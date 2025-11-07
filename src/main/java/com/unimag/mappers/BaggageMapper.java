package com.unimag.mappers;

import com.unimag.DTO.*;
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
    @Mapping(target = "tagCode", source = "tagCode")
    @Mapping(target = "ticket", source = "ticketId", qualifiedByName = "mapTicket")
    @Mapping(target = "fee", ignore = true)
    Baggage toEntity(BaggageDTO.BaggageCreateRequest dto);

    @Mapping(target = "fee", source = "fee")
    void updateEntity(BaggageDTO.BaggageUpdateRequest dto, @MappingTarget Baggage baggage);


    @Mapping(target = "ticketId", source = "ticket.id")
    @Mapping(target = "passengerName", source = "ticket.passenger.username")
    @Mapping(target = "tripInfo", source = "ticket.trip", qualifiedByName = "formatTripInfo")
    @Mapping(target = "excessWeight", ignore = true)
    BaggageDTO.BaggageResponse toResponse(Baggage entity);
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
}
