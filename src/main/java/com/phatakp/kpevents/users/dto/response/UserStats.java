package com.phatakp.kpevents.users.dto.response;

import com.fasterxml.jackson.annotation.JsonRawValue;

public record UserStats(
        String clerkId,
        String firstName,
        String lastName,
        Character building,
        Short flat,
        String committee,
        Double total,

        //Agg
        @JsonRawValue Object balanceByYear,
        @JsonRawValue Object balanceByYearAndTxnType,
        @JsonRawValue Object balanceByYearAndDonationType
) {
}
