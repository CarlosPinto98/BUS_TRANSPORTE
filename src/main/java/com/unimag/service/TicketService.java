package com.unimag.service;

import com.unimag.DTO.TicketDTO;
import com.unimag.entities.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TicketService {

    TicketDTO.ticketResponse create(TicketDTO.ticketCreateRequest request);
    TicketDTO.ticketResponse update(Long id, TicketDTO.ticketUpdateRequest request);
    void delete(Long id);
    Ticket getObject(long id);
    TicketDTO.ticketResponse get(Long id);
    Page<TicketDTO.ticketResponse> getAll(Pageable pageable);
    //List<RouteStop> findRouteStopsByUserId(Long userId, Long tripId);

}
