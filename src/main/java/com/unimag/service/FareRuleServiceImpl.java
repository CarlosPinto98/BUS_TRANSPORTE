package com.unimag.service;

import com.unimag.DTO.FareRuleDTO;
import com.unimag.entities.FareRule;
import com.unimag.exception.NotFoundException;
import com.unimag.mappers.FareRuleMapper;
import com.unimag.repository.FareRuleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor

public class FareRuleServiceImpl implements FareRuleService {

    private final FareRuleRepository fareRuleRepository;
    private final FareRuleMapper fareRuleMapper;
    private final StopService stopService;

    @Override
    public FareRuleDTO.fareRuleResponse save(FareRuleDTO.fareRuleCreateRequest createRequest) {
        var s = fareRuleMapper.toEntity(createRequest);
        if (createRequest.originId() == createRequest.destinationId()){
            throw new IllegalArgumentException("origen debe ser diferente a destino");
        }
        s.setFromStop(stopService.getObject(createRequest.originId()));
        s.setToStop(stopService.getObject(createRequest.destinationId()));
        return fareRuleMapper.toResponse(fareRuleRepository.save(s));
    }

    @Override
    public FareRuleDTO.fareRuleResponse get(Long id) {
        return fareRuleMapper.toResponse(getObject(id));
    }

    @Override
    public Page<FareRuleDTO.fareRuleResponse> getAll(Pageable pageable) {
        return  fareRuleRepository.findAll(pageable).map(fareRuleMapper::toResponse);
    }

    @Override
    public FareRule getObject(Long id) {
        return fareRuleRepository.findById(id).orElseThrow(() -> new NotFoundException("fareRule not found"));
    }

    @Override
    public boolean delete(Long id) {
        fareRuleRepository.deleteById(id);
        return true;
    }

    @Override
    public FareRuleDTO.fareRuleResponse update(FareRuleDTO.fareRuleUpdateRequest updateRequest, Long id) {
        var s =  getObject(id);
        if (updateRequest.originId() != null &&
                updateRequest.destinationId() != null &&
                Objects.equals(updateRequest.originId(), updateRequest.destinationId())) {

            throw new IllegalArgumentException("originId and destinationId cannot be the same");
        }
        fareRuleMapper.updateEntity(updateRequest, s);
        return fareRuleMapper.toResponse(s);
    }

}
