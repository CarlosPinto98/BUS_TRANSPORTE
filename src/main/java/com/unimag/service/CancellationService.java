package com.unimag.service;

import com.unimag.entities.Enums.CancellationPolicy;
import com.unimag.entities.Ticket;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Service
public interface CancellationService {

    CancellationPolicy determineCancellationPolicy(Ticket ticket);
    BigDecimal calculateRefundAmount(Ticket ticket, LocalDateTime cancellationTime);
    boolean canCancelTicket(Ticket ticket);
    String getCancellationReason(Ticket ticket);
}
