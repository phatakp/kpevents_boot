package com.phatakp.kpevents.admin.repos;

import com.phatakp.kpevents.admin.entity.Config;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigRepository extends JpaRepository<Config, Long> {
}