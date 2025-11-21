package com.unimag.service;

import com.unimag.DTO.TicketDTO;
import com.unimag.DTO.TicketDTO.*;
import com.unimag.entities.Enums.StatusTicket;
import com.unimag.entities.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TicketService {
    ticketResponse createTicket(ticketCreateRequest createRequest);
    ticketResponse updateTicket(Long id, ticketUpdateRequest request);
    ticketResponse getTicketById(Long id);
    ticketResponse getTicketByQrCode(String qrCode);
    ticketResponse getTicketWithDetails(Long id);
    List<ticketResponse> getAllTickets();
    List<ticketResponse> getTicketsByTripId(Long tripId);
    List<ticketResponse> getTicketsByPassengerId(Long passengerId);
    List<ticketResponse> getTicketsByTripAndStatus(Long tripId, StatusTicket status);
    void deleteTicket(Long id);
    ticketResponse cancelTicket(Long id);
    ticketResponse markAsNoShow(Long id);
    ticketResponse markAsUsed(Long id);
    boolean isSeatAvailable(Long tripId, String seatNumber);
    long countSoldTicketsByTrip(Long tripId);
    Ticket getObject(long id);


}
