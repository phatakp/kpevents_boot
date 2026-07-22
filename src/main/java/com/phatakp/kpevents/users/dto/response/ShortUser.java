package com.phatakp.kpevents.users.dto.response;

import com.phatakp.kpevents.common.enums.Building;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record ShortUser(
        String clerkId,
        String firstName,
        String lastName,
        Building building,
        Short flat
) implements Serializable {}