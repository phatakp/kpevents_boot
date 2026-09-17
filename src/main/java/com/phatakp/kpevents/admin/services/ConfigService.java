package com.phatakp.kpevents.admin.services;

import com.phatakp.kpevents.admin.entity.Config;
import com.phatakp.kpevents.admin.repos.ConfigRepository;
import com.phatakp.kpevents.common.exceptions.BusinessRuleException;
import com.phatakp.kpevents.common.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfigService {
    private final ConfigRepository configRepository;

    public Config getConfig() {
        return configRepository.findById(1L).orElseThrow(() -> new ResourceNotFoundException("Config",1L));
    }

    public Config updateConfig(@Valid Config request) {

        if (request.getActiveYear()==null || request.getActiveYear()<2025 || request.getActiveYear()>2100) {
            throw new BusinessRuleException("INVALID_YEAR","Year should be between 2025 and 2100");
        }
        Config config = getConfig();
        config.setActiveYear(request.getActiveYear());
        config.setIsAnnadaanActive(request.getIsAnnadaanActive());
        return configRepository.save(config);
    }
}
