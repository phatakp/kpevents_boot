package com.phatakp.kpevents.transactions.mapper;

import com.phatakp.kpevents.transactions.dto.request.TransactionRequest;
import com.phatakp.kpevents.transactions.dto.response.TransactionResponse;
import com.phatakp.kpevents.transactions.entity.Transaction;
import com.phatakp.kpevents.users.mappers.UserMapper;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {
    public static TransactionResponse toResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .description(transaction.getDescription())
                .amount(transaction.getAmount())
                .date(transaction.getDate())
                .committee(transaction.getCommittee())
                .year(transaction.getYear())
                .txnType(transaction.getTxnType())
                .txnMode(transaction.getTxnMode())
                .txnUser(UserMapper.toShortUser(transaction.getTxnUser()))
                .donation(DonationMapper.toResponse(transaction.getDonation()))
                .build();
    }

    public static Transaction toEntity(TransactionRequest request) {
        return Transaction.builder()
                .description(request.description())
                .amount(request.amount())
                .date(request.date())
                .committee(request.committee())
                .year(request.year())
                .txnType(request.txnType())
                .txnMode(request.txnMode())
                .build();
    }


}
