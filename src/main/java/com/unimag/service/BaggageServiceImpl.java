package com.unimag.service;

import com.unimag.DTO.BaggageDTO;
import com.unimag.entities.Baggage;
import com.unimag.entities.Ticket;
import com.unimag.exception.NotFoundException;
import com.unimag.mappers.BaggageMapper;
import com.unimag.repository.BaggageRepository;
import com.unimag.repository.TicketRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Transactional
@RequiredArgsConstructor

public class BaggageServiceImpl implements BaggageService {

    private BaggageRepository baggageRepository;
    private BaggageMapper baggageMapper;
    private final TicketRepository ticketRepository;

    private static final BigDecimal FREE_WEIGHT_KG = new BigDecimal("20.0");
    private static final BigDecimal PRICE_PER_KG = new BigDecimal("2000");


    @Override
    public BaggageDTO.baggageResponse create(BaggageDTO.baggageCreateRequest request) {

        Ticket ticket = ticketRepository.findById(request.ticketId())
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + request.ticketId()));

        Baggage baggage = baggageMapper.toEntity(request);
        baggage.setTicket(ticket);

        BigDecimal fee = calculateBaggageFee(request.weightKg());
        baggage.setFee(fee);

        Baggage savedBaggage = baggageRepository.save(baggage);
        return baggageMapper.toResponse(savedBaggage);
    }

    @Override
    public BaggageDTO.baggageResponse save(BaggageDTO.baggageCreateRequest createRequest) {
        BigDecimal fee = BigDecimal.ZERO;

        if (createRequest.weightKg().compareTo(BigDecimal.valueOf(5)) >= 0){
            fee = BigDecimal.valueOf(22);
        }

        Baggage baggage = baggageMapper.toEntity(createRequest);
        baggage.setFee(fee);
        Baggage saved = baggageRepository.save(baggage);
        return baggageMapper.toResponse(saved);
    }

    @Override
    public BaggageDTO.baggageResponse get(Long id) {
        return baggageMapper.toResponse(getObject(id));
    }

    @Override
    public BaggageDTO.baggageResponse get(String tagCode) {
        var f = baggageRepository.findByTagCode(tagCode).orElseThrow(() -> new NotFoundException("Baggage not found"));
        return baggageMapper.toResponse(f);
    }

    @Override
    public Page<BaggageDTO.baggageResponse> getAll(PageRequest pageRequest) {
        Page<Baggage> baggage = baggageRepository.findAll(pageRequest);
        return baggage.map(baggage1 -> baggageMapper.toResponse(baggage1));
    }

    @Override
    public boolean delete(Long id) {
        var f = getObject(id);
        var check = false;
        if (f!=null){
            baggageRepository.deleteById(id);
            check = true;
        }
        return check;
    }

    @Override
    public BaggageDTO.baggageResponse update(BaggageDTO.baggageUpdateRequest request, Long id) {
        var f =getObject(id);
        baggageMapper.updateEntity(request, f);
        return baggageMapper.toResponse(f);
    }

    @Override
    public Baggage getObject(Long id) {
        return baggageRepository.findById(id).orElseThrow(() -> new NotFoundException("baggage not found"));
    }

    public BigDecimal calculateBaggageFee(BigDecimal weightKg) {
        if (weightKg.compareTo(FREE_WEIGHT_KG) <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal excessWeight = weightKg.subtract(FREE_WEIGHT_KG);
        return excessWeight.multiply(PRICE_PER_KG);
    }

    public BigDecimal getTotalWeightByTrip(Long tripId) {
        BigDecimal totalWeight = baggageRepository.getTotalWeightByTrip(tripId);
        return totalWeight != null ? totalWeight : BigDecimal.ZERO;
    }
}
