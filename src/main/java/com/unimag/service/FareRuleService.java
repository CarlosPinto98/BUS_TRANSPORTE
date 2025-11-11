package com.unimag.service;

import com.unimag.DTO.FareRuleDTO;
import com.unimag.entities.FareRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FareRuleService {

    FareRuleDTO.fareRuleResponse save(FareRuleDTO.fareRuleCreateRequest request);
    FareRuleDTO.fareRuleResponse get(Long id);
    Page<FareRuleDTO.fareRuleResponse> getAll(Pageable pageable);
    FareRule getObject(Long id);
    boolean delete(Long id);
    FareRuleDTO.fareRuleResponse update(FareRuleDTO.fareRuleUpdateRequest request, Long id);
}
