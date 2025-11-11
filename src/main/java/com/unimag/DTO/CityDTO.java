package com.unimag.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Set;

public class CityDTO {

    public record cityCreateRequest(@NotBlank String name, @NotNull float lat, @NotNull float lon) implements Serializable {}
    public record cityUpdateRequest(String name, float lat, float lon) implements Serializable {}
    public record cityResponse(Long id , String name, float lat, float lon, Set<stopDTO> stops) implements  Serializable{}

    public record stopDTO(Long id, String name, float lat, float lng) implements Serializable{}
}
