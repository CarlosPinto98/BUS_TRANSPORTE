package com.unimag.service;

import com.unimag.entities.Ticket;

public interface SegmentValidationService {

    boolean isSeatAvailableForSegment(Long tripId, String seatNumber, Integer fromStopOrder, Integer toStopOrder);
    boolean isSegmentOverlap(Ticket existingTicket, Integer newFromOrder, Integer newToOrder);
    void validateSegment(Long tripId, String seatNumber, Integer fromStopOrder, Integer toStopOrder);
    void releaseSeatForSegment(Long tripId, String seatNumber, Integer stopOrder);
}
