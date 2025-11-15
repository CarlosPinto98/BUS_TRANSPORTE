package com.unimag.service;

import com.unimag.DTO.TicketDTO;
import com.unimag.entities.*;
import com.unimag.entities.Enums.*;

import com.unimag.mappers.TicketMapper;
import com.unimag.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor

public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final StopRepository stopRepository;
    private final FareRuleRepository fareRuleRepository;
    private final SeatHoldRepository seatHoldRepository;
    private final TicketMapper ticketMapper;
    private final SegmentValidationService segmentValidationService;
    private final DiscountService discountService;
    private final CancellationService cancellationService;

    @Override
    public TicketDTO.ticketResponse createTicket(TicketDTO.ticketCreateRequest createRequest) {

        Trip trip = tripRepository.findById(createRequest.tripId())
                .orElseThrow(() -> new IllegalArgumentException("Trip not found: " + createRequest.tripId()));

        User passenger = userRepository.findById(createRequest.passengerId())
                .orElseThrow(() -> new IllegalArgumentException("Passenger not found: " + createRequest.passengerId()));

        Stop fromStop = stopRepository.findById(createRequest.fromStopId())
                .orElseThrow(() -> new IllegalArgumentException("From stop not found: " + createRequest.fromStopId()));

        Stop toStop = stopRepository.findById(createRequest.toStopId())
                .orElseThrow(() -> new IllegalArgumentException("To stop not found: " + createRequest.toStopId()));

        if (fromStop.getOrder() >= toStop.getOrder()) {
            throw new IllegalArgumentException(String.format(
                    "Invalid stop sequence: fromStop (%d) must be before toStop (%d)",
                    fromStop.getOrder(), toStop.getOrder()
            ));
        }

        segmentValidationService.validateSegment(
                trip.getId(),
                createRequest.seatNumber(),
                fromStop.getOrder(),
                toStop.getOrder()
        );

        BigDecimal basePrice = calculateFare(trip.getRoute().getId(),
                createRequest.fromStopId(), createRequest.toStopId());

        PassengerType passengerType = discountService.determinePassengerType(
                passenger.getAge(), null);

        BigDecimal discount = discountService.calculateDiscount(passengerType, basePrice);
        BigDecimal finalPrice = basePrice.subtract(discount).max(BigDecimal.ZERO);

        Ticket ticket = ticketMapper.toEntity(createRequest);
        ticket.setTrip(trip);
        ticket.setPassenger(passenger);
        ticket.setFromStop(fromStop);
        ticket.setToStop(toStop);
        ticket.setPrice(finalPrice);
        ticket.setStatusTicket(StatusTicket.SOLD);
        ticket.setDiscountAmount(discount);
        ticket.setCreatedAt(LocalDateTime.now());



        Ticket savedTicket = ticketRepository.save(ticket);
        log.info("""
             Ticket creado exitosamente:
            ▸ ID: {}
            ▸ Pasajero: {}
            ▸ Tipo: {}
            ▸ Precio base: {}
            ▸ Descuento aplicado: {}
            ▸ Precio final: {}
            """,
                savedTicket.getId(), passenger.getName(),
                passengerType, basePrice, discount,
                finalPrice
        );
        return ticketMapper.toResponse(savedTicket);
    }

    @Override
    public TicketDTO.ticketResponse updateTicket(Long id, TicketDTO.ticketUpdateRequest request) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + id));

        ticketMapper.updateEntity(request, ticket);
        Ticket updatedTicket = ticketRepository.save(ticket);
        return ticketMapper.toResponse(updatedTicket);
    }

    @Override
    public TicketDTO.ticketResponse getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + id));
        return ticketMapper.toResponse(ticket);
    }

    @Override
    public TicketDTO.ticketResponse getTicketByQrCode(String qrCode) {
        Ticket ticket = ticketRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found with QR: " + qrCode));
        return ticketMapper.toResponse(ticket);
    }

    @Override
    public TicketDTO.ticketResponse getTicketWithDetails(Long id) {
        Ticket ticket = ticketRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + id));
        return ticketMapper.toResponse(ticket);
    }

    @Override
    public List<TicketDTO.ticketResponse> getAllTickets() {
        return ticketRepository.findAll().stream()
                .map(ticketMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TicketDTO.ticketResponse> getTicketsByTripId(Long tripId) {
        if (!tripRepository.existsById(tripId)) {
            throw new IllegalArgumentException("Trip not found: " + tripId);
        }
        return ticketRepository.findByTripIdAndStatusTicket(tripId, StatusTicket.SOLD).stream()
                .map(ticketMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TicketDTO.ticketResponse> getTicketsByPassengerId(Long passengerId) {
        if (!userRepository.existsById(passengerId)) {
            throw new IllegalArgumentException("Passenger not found: " + passengerId);
        }
        return ticketRepository.findByPassengerId(passengerId).stream()
                .map(ticketMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TicketDTO.ticketResponse> getTicketsByTripAndStatus(Long tripId, StatusTicket status) {
        if (!tripRepository.existsById(tripId)) {
            throw new IllegalArgumentException("Trip not found: " + tripId);
        }
        return ticketRepository.findByTripIdAndStatusTicket(tripId, status).stream()
                .map(ticketMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTicket(Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new IllegalArgumentException("Ticket not found: " + id);
        }
        ticketRepository.deleteById(id);
    }

    @Override
    public TicketDTO.ticketResponse cancelTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + id));

        if (ticket.getStatusTicket() == StatusTicket.CANCELLED) {
            throw new IllegalStateException("Ticket already cancelled");
        }

        if (!cancellationService.canCancelTicket(ticket)) {
            String reason = cancellationService.getCancellationReason(ticket);
            throw new IllegalArgumentException("Cannot cancel ticket: " + reason);
        }

        BigDecimal refundAmount = cancellationService.calculateRefundAmount(ticket, LocalDateTime.now());
        CancellationPolicy policy = cancellationService.determineCancellationPolicy(ticket);

        ticket.setStatusTicket(StatusTicket.CANCELLED);
        ticket.setCancelledAt(LocalDateTime.now());
        ticket.setRefundAmount(refundAmount);
        ticket.setCancellationPolicy(policy);

        Ticket updatedTicket = ticketRepository.save(ticket);

        if (refundAmount.compareTo(BigDecimal.ZERO) > 0) {
            processRefund(ticket.getPassenger().getId(), refundAmount, ticket.getId());
        }
        log.info("""
            🎟️ Ticket cancelado exitosamente:
              • ID: {}
              • Pasajero: {}
              • Política: {}
              • Reembolso: {}
            """,
                id, ticket.getPassenger().getName(), policy, refundAmount
        );

        return ticketMapper.toResponse(updatedTicket);
    }

    @Override
    public TicketDTO.ticketResponse markAsNoShow(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + id));

        if (ticket.getStatusTicket() != StatusTicket.SOLD) {
            throw new IllegalArgumentException("Invalid ticket status for no-show");
        }

        ticket.setStatusTicket(StatusTicket.NO_SHOW);
        Ticket updatedTicket = ticketRepository.save(ticket);
        return ticketMapper.toResponse(updatedTicket);
    }

    @Override
    public TicketDTO.ticketResponse markAsUsed(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + id));

        if (ticket.getStatusTicket() != StatusTicket.SOLD) {
            throw new IllegalArgumentException("Can only mark sold tickets as used");
        }
        ticket.setStatusTicket(StatusTicket.USED);
        Ticket updatedTicket = ticketRepository.save(ticket);
        return ticketMapper.toResponse(updatedTicket);
    }

    @Override
    public boolean isSeatAvailable(Long tripId, String seatNumber) {
        boolean ticketExists = ticketRepository.findSoldTicketBySeat(tripId, seatNumber).isPresent();

        boolean holdExists = seatHoldRepository.existsByTripIdAndSeatNumberAndStatusSeatHold(
                tripId, seatNumber, StatusSeatHold.HOLD);
        return !ticketExists && !holdExists;
    }

    @Override
    public long countSoldTicketsByTrip(Long tripId) {
            return ticketRepository.countSoldTicketsByTrip(tripId);
        }
        private BigDecimal calculateFare(Long routeId, Long fromStopId, Long toStopId) {
            return fareRuleRepository.findFareForSegment(routeId, fromStopId, toStopId)
                    .map(FareRule::getBasePrice)
                    .orElse(new BigDecimal("50000")); // Precio por defecto
        }

    @Override
    public Ticket getObject(long id) {
        return null;
    }

    private void processRefund(Long passengerId, BigDecimal amount, Long ticketId) {
        try {
            log.info(" Procesando reembolso de {} para pasajero {} (ticket {})",
                    amount, passengerId, ticketId);
        } catch (Exception ex) {
            log.error("Error al procesar reembolso para ticket {}: {}", ticketId, ex.getMessage());
            throw new IllegalStateException("Refund processing failed", ex);
        }
    }
}



