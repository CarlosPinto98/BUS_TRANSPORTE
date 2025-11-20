package com.unimag.controllers;

import com.unimag.DTO.TicketDTO;
import com.unimag.entities.Enums.StatusTicket;
import com.unimag.security.service.CustomUserDetails;
import com.unimag.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
@Validated
@Slf4j
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('PASSENGER', 'CLERK', 'ADMIN')")
    public ResponseEntity<TicketDTO.ticketResponse> createTicket(
            @Valid @RequestBody TicketDTO.ticketCreateRequest request
    ) {
        log.info("Creating ticket for trip {} seat {}", request.tripId(), request.seatNumber());

        TicketDTO.ticketResponse created = ticketService.createTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/my-tickets")
    @PreAuthorize("hasAnyRole('PASSENGER', 'ADMIN')")
    public ResponseEntity<List<TicketDTO.ticketResponse>> getMyTickets(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long passengerId = userDetails.getId();

        log.debug("User {} retrieving their tickets", passengerId);

        List<TicketDTO.ticketResponse> tickets = ticketService.getTicketsByPassengerId(passengerId);
        return ResponseEntity.ok(tickets);
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('PASSENGER', 'CLERK', 'ADMIN')")
    public ResponseEntity<TicketDTO.ticketResponse> cancelTicket(@PathVariable Long id) {
        log.info("Cancelling ticket: {}", id);

        TicketDTO.ticketResponse cancelled = ticketService.cancelTicket(id);
        return ResponseEntity.ok(cancelled);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('CLERK', 'ADMIN')")
    public ResponseEntity<List<TicketDTO.ticketResponse>> getAllTickets() {
        log.debug("Retrieving all tickets");

        List<TicketDTO.ticketResponse> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLERK', 'DRIVER', 'ADMIN')")
    public ResponseEntity<TicketDTO.ticketResponse> getTicketById(@PathVariable Long id) {
        log.debug("Retrieving ticket: {}", id);

        TicketDTO.ticketResponse ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/{id}/details")
    @PreAuthorize("hasAnyRole('CLERK', 'DRIVER', 'ADMIN')")
    public ResponseEntity<TicketDTO.ticketResponse> getTicketWithDetails(@PathVariable Long id) {
        log.debug("Retrieving ticket with details: {}", id);

        TicketDTO.ticketResponse ticket = ticketService.getTicketWithDetails(id);
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/qr/{qrCode}")
    @PreAuthorize("hasAnyRole('CLERK', 'DRIVER', 'ADMIN')")
    public ResponseEntity<TicketDTO.ticketResponse> getTicketByQrCode(@PathVariable String qrCode) {
        log.debug("Retrieving ticket by QR code: {}", qrCode);

        TicketDTO.ticketResponse ticket = ticketService.getTicketByQrCode(qrCode);
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/trip/{tripId}")
    @PreAuthorize("hasAnyRole('CLERK', 'DRIVER', 'DISPATCHER', 'ADMIN')")
    public ResponseEntity<List<TicketDTO.ticketResponse>> getTicketsByTrip(@PathVariable Long tripId) {
        log.debug("Retrieving tickets for trip: {}", tripId);

        List<TicketDTO.ticketResponse> tickets = ticketService.getTicketsByTripId(tripId);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/trip/{tripId}/status/{status}")
    @PreAuthorize("hasAnyRole('CLERK', 'DRIVER', 'DISPATCHER', 'ADMIN')")
    public ResponseEntity<List<TicketDTO.ticketResponse>> getTicketsByTripAndStatus(
            @PathVariable Long tripId,
            @PathVariable StatusTicket status
    ) {
        log.debug("Retrieving tickets for trip {} with status: {}", tripId, status);

        List<TicketDTO.ticketResponse> tickets = ticketService.getTicketsByTripAndStatus(tripId, status);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/passenger/{passengerId}")
    @PreAuthorize("hasAnyRole('CLERK', 'ADMIN')")
    public ResponseEntity<List<TicketDTO.ticketResponse>> getTicketsByPassenger(@PathVariable Long passengerId) {
        log.debug("Retrieving tickets for passenger: {}", passengerId);

        List<TicketDTO.ticketResponse> tickets = ticketService.getTicketsByPassengerId(passengerId);
        return ResponseEntity.ok(tickets);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('CLERK', 'ADMIN')")
    public ResponseEntity<TicketDTO.ticketResponse> updateTicket(
            @PathVariable Long id,
            @Valid @RequestBody TicketDTO.ticketUpdateRequest request
    ) {
        log.info("Updating ticket: {}", id);

        TicketDTO.ticketResponse updated = ticketService.updateTicket(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        log.warn("Deleting ticket: {}", id);

        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/used")
    @PreAuthorize("hasAnyRole('DRIVER', 'DISPATCHER', 'ADMIN')")
    public ResponseEntity<TicketDTO.ticketResponse> markAsUsed(@PathVariable Long id) {
        log.info("Marking ticket as used: {}", id);

        TicketDTO.ticketResponse updated = ticketService.markAsUsed(id);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/no-show")
    @PreAuthorize("hasAnyRole('DRIVER', 'DISPATCHER', 'ADMIN')")
    public ResponseEntity<TicketDTO.ticketResponse> markAsNoShow(@PathVariable Long id) {
        log.info("Marking ticket as no-show: {}", id);

        TicketDTO.ticketResponse updated = ticketService.markAsNoShow(id);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/check-availability")
    @PreAuthorize("hasAnyRole('CLERK', 'DRIVER', 'DISPATCHER', 'ADMIN')")
    public ResponseEntity<Boolean> isSeatAvailable(
            @RequestParam Long tripId,
            @RequestParam String seatNumber
    ) {
        log.debug("Checking seat {} availability for trip: {}", seatNumber, tripId);

        boolean available = ticketService.isSeatAvailable(tripId, seatNumber);
        return ResponseEntity.ok(available);
    }

    @GetMapping("/trip/{tripId}/count")
    @PreAuthorize("hasAnyRole('CLERK', 'DRIVER', 'DISPATCHER', 'ADMIN')")
    public ResponseEntity<Long> countSoldTicketsByTrip(@PathVariable Long tripId) {
        log.debug("Counting sold tickets for trip: {}", tripId);

        long count = ticketService.countSoldTicketsByTrip(tripId);
        return ResponseEntity.ok(count);
    }
}
