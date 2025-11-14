package com.unimag.service;

import com.unimag.DTO.TicketDTO;
import com.unimag.entities.Ticket;
import com.unimag.exception.NotFoundException;
import com.unimag.mappers.TicketMapper;
import com.unimag.repository.TicketRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor

public class TicketServiceImpl implements  TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    private final TripService tripService;
    private final StopService stopService;
    private final UserService userService;

    @Override
    public TicketDTO.ticketResponse create(TicketDTO.ticketCreateRequest request) {
        return null;
    }

    @Override
    public TicketDTO.ticketResponse update(Long id, TicketDTO.ticketUpdateRequest request) {
        return null;
    }

    @Override
    public void delete(Long id) {

        return;
    }

    @Override
    public Ticket getObject(long id) {
        return ticketRepository.findById(id).orElseThrow(()-> new NotFoundException("Ticket not found"));
    }

    @Override
    public TicketDTO.ticketResponse get(Long id) {
        return ticketMapper.toResponse(getObject(id));
    }

    @Override
    public Page<TicketDTO.ticketResponse> getAll(Pageable pageable) {
        return ticketRepository.findAll(pageable).map(ticketMapper::toResponse);
    }
}
