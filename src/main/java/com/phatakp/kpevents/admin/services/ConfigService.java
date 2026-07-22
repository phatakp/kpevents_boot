package com.phatakp.kpevents.admin.services;

import com.phatakp.kpevents.admin.entity.Config;
import com.phatakp.kpevents.admin.repos.ConfigRepository;
import com.phatakp.kpevents.common.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfigService {
    private final ConfigRepository configRepository;

    public Config getConfig() {
        return configRepository.findById(1L).orElseThrow(() -> new ResourceNotFoundException("Config",1L));
    }
}
