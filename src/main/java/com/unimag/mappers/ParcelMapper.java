package com.unimag.mappers;

import com.unimag.DTO.ParcelDTO.*;
import com.unimag.entities.Enums.StatusParcel;
import com.unimag.entities.Parcel;
import com.unimag.entities.Stop;
import com.unimag.entities.Trip;
import org.mapstruct.*;

import java.util.Random;

@Mapper(componentModel = "spring")
public interface ParcelMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "statusParcel", ignore = true)  // Se establece en @AfterMapping
    @Mapping(target = "senderName", source = "senderName")
    @Mapping(target = "senderPhone", source = "senderPhone")
    @Mapping(target = "receiverName", source = "receiverName")
    @Mapping(target = "receiverPhone", source = "receiverPhone")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "fromStop", source = "fromStopId", qualifiedByName = "mapStop")
    @Mapping(target = "toStop", source = "toStopId", qualifiedByName = "mapStop")
    @Mapping(target = "trip", source = "tripId", qualifiedByName = "mapTrip")
    @Mapping(target = "proofPhotoUrl", ignore = true)
    @Mapping(target = "deliveryOtp", ignore = true)  // Se establece en @AfterMapping
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deliveredAt", ignore = true)
    Parcel toEntity(ParcelCreateRequest dto);

    @AfterMapping
    default void generateCodeAndOtp(ParcelCreateRequest request, @MappingTarget Parcel parcel) {
        parcel.setCode("PCL-" + System.currentTimeMillis());

        Random random = new Random();
        String otp = String.format("%06d", random.nextInt(1000000));
        parcel.setDeliveryOtp(otp);

        parcel.setStatusParcel(StatusParcel.CREATED);  // Establece status aquí
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "senderName", ignore = true)
    @Mapping(target = "senderPhone", ignore = true)
    @Mapping(target = "receiverName", ignore = true)
    @Mapping(target = "receiverPhone", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "fromStop", ignore = true)
    @Mapping(target = "toStop", ignore = true)
    @Mapping(target = "trip", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deliveredAt", ignore = true)
    // ← NO pongas @Mapping para statusParcel aquí, MapStruct mapeará automáticamente "delivered" → "statusParcel"
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
    void updateEntity(ParcelUpdateRequest dto, @MappingTarget Parcel parcel);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    @Mapping(target = "statusParcel", source = "statusParcel")
    @Mapping(target = "fromStopId", source = "fromStop.id")
    @Mapping(target = "toStopId", source = "toStop.id")
    @Mapping(target = "tripId", source = "trip.id")
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