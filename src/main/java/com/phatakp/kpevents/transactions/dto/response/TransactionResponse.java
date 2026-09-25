package com.phatakp.kpevents.transactions.dto.response;

import com.phatakp.kpevents.common.enums.Committee;
import com.phatakp.kpevents.common.enums.TxnMode;
import com.phatakp.kpevents.common.enums.TxnType;
import com.phatakp.kpevents.transactions.entity.Transaction;
import com.phatakp.kpevents.transactions.mapper.DonationMapper;
import com.phatakp.kpevents.users.dto.response.ShortUser;
import com.phatakp.kpevents.users.mappers.UserMapper;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link com.phatakp.kpevents.transactions.entity.Transaction}
 */
@Builder
public record TransactionResponse(
        String id,
        String description,
        Float amount,
        LocalDate date,

        @Enumerated(EnumType.STRING)
        Committee committee,
        Short year,
        ShortUser txnUser,

        @Enumerated(EnumType.STRING)
        TxnType txnType,

        @Enumerated(EnumType.STRING)
        TxnMode txnMode,

        DonationResponse donation


) implements Serializable {
        public static TransactionResponse fromEntity(Transaction txn) {
                return new TransactionResponse(
                        txn.getId(),
                        txn.getDescription(),
                        txn.getAmount(),
                        txn.getDate(),
                        txn.getCommittee(),
                        txn.getYear(),
                        UserMapper.toShortUser(txn.getTxnUser()),
                        txn.getTxnType(),
                        txn.getTxnMode(),
                        DonationMapper.toResponse(txn.getDonation())
                );
        }
}