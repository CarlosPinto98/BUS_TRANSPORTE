package com.unimag.entities;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Map;

@Converter
public class AmenitiesConverter implements AttributeConverter<Map<String, Object>, String> {

    private final AmenitiesConverter amenitiesConverter = new AmenitiesConverter();


    @Override
    public String convertToDatabaseColumn(Map<String, Object> stringObjectMap) {
        return "";
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String s) {
        return Map.of();
    }
}