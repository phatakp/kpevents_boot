package com.phatakp.kpevents.transactions.dto.response;

public record LinkedTransfer(
        String txnId,
        String fromUserId,
        String toUserId
) {
}
