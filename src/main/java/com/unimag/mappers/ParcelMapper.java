package com.unimag.mappers;

import com.unimag.DTO.ParcelDTO.*;
import com.unimag.entities.Parcel;
import com.unimag.entities.Stop;
import com.unimag.entities.Trip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ParcelMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", source = "code")
    @Mapping(target = "senderName", source = "senderName")
    @Mapping(target = "senderPhone", source = "senderPhone")
    @Mapping(target = "receiverName", source = "receiverName")
    @Mapping(target = "receiverPhone", source = "receiverPhone")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "fromStop", source = "fromStopId", qualifiedByName = "mapStop")
    @Mapping(target = "toStop", source = "toStopId", qualifiedByName = "mapStop")
    @Mapping(target = "trip", source = "tripId", qualifiedByName = "mapTrip")
    @Mapping(target = "status", expression = "java(ParcelStatus.CREATED)")
    @Mapping(target = "proofPhotoUrl", ignore = true)
    @Mapping(target = "deliveryOtp", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deliveredAt", ignore = true)
    Parcel toEntity(ParcelCreateRequest dto);


    @Mapping(target = "status", source = "status")
    @Mapping(target = "proofPhotoUrl", source = "proofPhotoUrl")
    @Mapping(target = "deliveryOtp", source = "deliveryOtp")
    void updateEntity(ParcelUpdateRequest dto, @MappingTarget Parcel parcel);


    @Mapping(target = "fromStopId", source = "fromStop.id")
    @Mapping(target = "toStopId", source = "toStop.id")
    @Mapping(target = "tripId", source = "trip.id")
    @Mapping(target = "status", source = "status")
    ParcelResponse toResponse(Parcel entity);

    @Named("mapStop")
    default Stop mapStop(Long id) {
        if (id == null) return null;
        Stop s = new Stop();
        s.setId(id);
        return s;
    }

    @Named("mapTrip")
    default Trip mapTrip(Long id) {
        if (id == null) return null;
        Trip t = new Trip();
        t.setId(id);
        return t;
    }
}
