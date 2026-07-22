package com.phatakp.kpevents.users.dto.response;

import com.phatakp.kpevents.common.enums.Committee;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;

@Builder
public record UserMembership(
        @Enumerated(EnumType.STRING)
        Committee committee,
        Boolean isActive
) {
}
