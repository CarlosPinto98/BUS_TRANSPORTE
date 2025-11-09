package com.unimag.mappers;

import com.unimag.DTO.ConfigDTO.*;
import com.unimag.entities.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ConfigMapper Tests")
class ConfigMapperTest {

    private ConfigMapper configMapper;

    @BeforeEach
    void setUp() {
        configMapper = Mappers.getMapper(ConfigMapper.class);
    }

    @Test
    @DisplayName("Debe mapear ConfigCreateRequest a la entidad Config")
    void toEntity() {

        ConfigCreateRequest request = new ConfigCreateRequest(
                "seat.hold.minutes",
                "10",
                "Minutes to hold a seat"
        );

        Config config = configMapper.toEntity(request);

        assertNotNull(config);
        assertEquals("seat.hold.minutes", config.getKey());
        assertEquals("10", config.getValue());
        assertEquals("Minutes to hold a seat", config.getDescription());
        assertNull(config.getId());
    }

    @Test
    void updateEntity() {
    }

    @Test
    @DisplayName("Debe mapear la entidad Config a ConfigResponse")
    void toResponse() {

        LocalDateTime updatedAt = LocalDateTime.now();

        Config config = Config.builder()
                .id(1L)
                .key("overbooking.percentage")
                .value("5")
                .description("Max overbooking percentage")
                .updatedAt(updatedAt)
                .build();

        ConfigResponse response = configMapper.toResponse(config);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("overbooking.percentage", response.key());
        assertEquals("5", response.value());
        assertEquals("Max overbooking percentage", response.description());
        assertEquals(updatedAt, response.updatedAt());
    }
}
