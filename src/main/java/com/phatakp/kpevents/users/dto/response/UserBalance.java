package com.phatakp.kpevents.users.dto.response;

import com.phatakp.kpevents.common.enums.Building;
import lombok.Builder;

import java.io.Serializable;
import java.util.List;

@Builder
public record UserBalance(
        String clerkId,
        String firstName,
        String lastName,
        Building building,
        Short flat,
        Double total,
        List<BalanceStat> balances
) implements Serializable {}