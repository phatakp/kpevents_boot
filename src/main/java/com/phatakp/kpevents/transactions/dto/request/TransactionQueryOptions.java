package com.phatakp.kpevents.transactions.dto.request;

import com.phatakp.kpevents.common.enums.*;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record TransactionQueryOptions(
        @NotNull(message = "Committee is required")
        @Enumerated(EnumType.STRING)
        Committee committee,

        @NotNull(message = "Txn Type is required")
        @Enumerated(EnumType.STRING)
        TxnType txnType,

        @NotNull(message = "Year is required")
        Short year,

        @Enumerated(EnumType.STRING)
        Building building,

        @Enumerated(EnumType.STRING)
        DonationType donationType,

        @Enumerated(EnumType.STRING)
        TxnMode txnMode,

        String txnUserId,
        String userName,
        String searchTerm,

        int page,
        int size
) {

}
