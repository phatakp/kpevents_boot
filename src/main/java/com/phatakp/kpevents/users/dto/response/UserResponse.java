package com.phatakp.kpevents.users.dto.response;

import com.phatakp.kpevents.common.enums.Building;
import com.phatakp.kpevents.common.enums.UserRole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link com.phatakp.kpevents.users.entity.User}
 */
@Builder
public record UserResponse(
        String clerkId,
        String email,
        String firstName,
        String lastName,
        String imageUrl,

        @Enumerated(EnumType.STRING)
        UserRole role,

        @Enumerated(EnumType.STRING)
        Building building,

        Short flat,

        List<UserMembership> memberships


) implements Serializable {
}