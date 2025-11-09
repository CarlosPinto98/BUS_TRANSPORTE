package com.unimag.mappers;

import com.unimag.DTO.FareRuleDTO.*;
import com.unimag.entities.Enums.DynamicPricing;
import com.unimag.entities.FareRule;
import com.unimag.entities.Route;
import com.unimag.entities.Stop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FareRuleMapper Tests")
class FareRuleMapperTest {

    private FareRuleMapper fareRuleMapper;
    @BeforeEach
    void setUp() {
        fareRuleMapper = Mappers.getMapper(FareRuleMapper.class);
    }

    @Test
    @DisplayName("Debe mapear FareRuleCreateRequest a la entidad FareRule")
    void toEntity() {

        Map<String, Object> discounts = new HashMap<>();
        discounts.put("student", 0.15);
        discounts.put("senior", 0.20);

        FareRuleCreateRequest request = new FareRuleCreateRequest(
                new BigDecimal("50000"),
                discounts,
                DynamicPricing.ON,
                1L,
                2L,
                3L
        );

        FareRule fareRule = fareRuleMapper.toEntity(request);

        assertNotNull(fareRule);
        assertEquals(new BigDecimal("50000"), fareRule.getBasePrice());
        assertEquals(DynamicPricing.ON, fareRule.getDynamicPricing());
        assertNotNull(fareRule.getDiscounts());
        assertEquals(2, fareRule.getDiscounts().size());
    }

    @Test
    @DisplayName("Debe mapear la entidad FareRule a FareRuleResponse")
    void toResponse() {

        Route route = Route.builder().id(1L).build();
        Stop fromStop = Stop.builder().id(2L).name("Bogotá").build();
        Stop toStop = Stop.builder().id(3L).name("Tunja").build();

        Map<String, Object> discounts = new HashMap<>();
        discounts.put("child", 0.25);

        FareRule fareRule = FareRule.builder()
                .id(1L)
                .basePrice(new BigDecimal("45000"))
                .discounts(discounts)
                .dynamicPricing(DynamicPricing.OFF)
                .route(route)
                .fromStop(fromStop)
                .toStop(toStop)
                .build();

        FareRuleResponse response = fareRuleMapper.toResponse(fareRule);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(new BigDecimal("45000"), response.basePrice());
        assertEquals("OFF", response.dynamicPricing());
        assertEquals(1L, response.routeId());
        assertEquals(2L, response.fromStopId());
        assertEquals("Bogotá", response.fromStopName());
        assertEquals(3L, response.toStopId());
        assertEquals("Tunja", response.toStopName());
    }

    @Test
    void mapRoute() {
    }

    @Test
    void mapStop() {
    }

    @Test
    void updateEntity() {
    }
}


