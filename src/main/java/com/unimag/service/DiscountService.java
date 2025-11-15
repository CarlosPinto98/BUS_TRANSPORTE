package com.unimag.service;

import com.unimag.entities.Enums.PassengerType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
@Service
public interface DiscountService {

    BigDecimal calculateDiscount(PassengerType passengerType, BigDecimal basePrice);
    BigDecimal applyDiscount(PassengerType passengerType, BigDecimal basePrice);
    boolean validatePassengerAge(PassengerType passengerType, Integer age);
    PassengerType determinePassengerType(Integer age, Boolean isStudent);
}
