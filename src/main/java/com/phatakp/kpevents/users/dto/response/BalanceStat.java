package com.phatakp.kpevents.users.dto.response;

import com.phatakp.kpevents.common.enums.Committee;
import com.phatakp.kpevents.common.enums.TxnType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;

@Builder
public record BalanceStat(
        @Enumerated(EnumType.STRING)
        Committee committee,

        Short year,

        @Enumerated(EnumType.STRING)
        TxnType txnType,

        String donationType,
        Float balance
) {
}
