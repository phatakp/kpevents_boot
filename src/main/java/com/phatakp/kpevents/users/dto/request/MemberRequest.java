package com.phatakp.kpevents.users.dto.request;

import com.phatakp.kpevents.common.enums.Committee;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MemberRequest(
        @NotBlank(message = "User ID is required")
        String userId,

        @NotNull(message = "Committee is required")
        Committee committee
) {
}
