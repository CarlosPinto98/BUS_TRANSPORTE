package com.unimag.service;

import com.unimag.DTO.BaggageDTO;
import com.unimag.entities.Baggage;
import com.unimag.exception.NotFoundException;
import com.unimag.mappers.BaggageMapper;
import com.unimag.repository.BaggageRepository;
import jakarta.transaction.Transactional;
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
}
