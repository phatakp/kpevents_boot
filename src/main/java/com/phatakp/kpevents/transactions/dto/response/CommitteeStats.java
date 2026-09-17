package com.phatakp.kpevents.transactions.dto.response;

import com.fasterxml.jackson.annotation.JsonRawValue;

public record CommitteeStats(
        String committee,
        Double total,

        //Agg
        @JsonRawValue Object balanceByYear,
        @JsonRawValue Object balanceByYearAndTxnType,
        @JsonRawValue Object balanceByYearAndDonationType
) {
}
