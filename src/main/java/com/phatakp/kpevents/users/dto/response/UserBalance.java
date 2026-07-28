package com.phatakp.kpevents.users.dto.response;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record UserBalance(
        String clerkId,
        String firstName,
        String lastName,
        Character building,
        Short flat,
        String committee,
        Short year,
        String txnType,
        String donationType,
        Double balance
) implements Serializable {}