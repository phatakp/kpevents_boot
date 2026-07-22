package com.phatakp.kpevents.transactions.dto.response;

public record CommitteeBalanceResponse(
        String committee,
        Short year,
        String txnType,
        String donationType,
        Double balance
) {
}
