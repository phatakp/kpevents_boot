package com.phatakp.kpevents.transactions.strategies.transaction;

import com.phatakp.kpevents.common.enums.Building;
import com.phatakp.kpevents.common.enums.Committee;
import com.phatakp.kpevents.common.enums.DonationType;
import com.phatakp.kpevents.common.enums.TxnType;
import com.phatakp.kpevents.transactions.dto.request.TransactionRequest;
import com.phatakp.kpevents.transactions.dto.response.TransactionPageResponse;
import com.phatakp.kpevents.transactions.dto.response.TransactionResponse;
import com.phatakp.kpevents.transactions.entity.Transaction;
import com.phatakp.kpevents.transactions.mapper.TransactionMapper;
import com.phatakp.kpevents.transactions.repos.TransactionRepository;
import com.phatakp.kpevents.users.services.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class TransferTxnStrategy implements TransactionTypeStrategy {
    private final TransactionRepository transactionRepository;
    private final MemberService memberService;

    @Override
    public TransactionResponse process(TransactionRequest request) {
        String userId = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        var logUser = memberService.assertIsCommitteeMember(request.committee(),userId,"Add Transfer");
        var fromUser = memberService.assertIsCommitteeMember(request.committee(), request.txnUserId(), "Make Transfer");
        var toUser = memberService.assertIsCommitteeMember(request.committee(), request.toUserId(), "Receive Transfer");

        Transaction fromTxn = TransactionMapper.toEntity(request);
        fromTxn.setAmount(request.amount()*-1);
        fromTxn.setTxnUser(fromUser);
        fromTxn.setLogUser(logUser);
        fromTxn.setDescription("Transferred to " + toUser.getUserInfo());

        Transaction toTxn = TransactionMapper.toEntity(request);
        toTxn.setTxnUser(toUser);
        toTxn.setLogUser(logUser);
        toTxn.setLinked(fromTxn);
        toTxn.setDescription("Received from " + fromUser.getUserInfo());
        transactionRepository.saveAll(List.of(toTxn, fromTxn));

        return TransactionMapper.toResponse(toTxn);

    }

    @Override
    public TransactionResponse update(Transaction toTxn, TransactionRequest request) {
        String userId = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        var logUser = memberService.assertIsCommitteeMember(request.committee(),userId,"Update Transfer");
        var fromUser = memberService.assertIsCommitteeMember(request.committee(), request.txnUserId(), "Make Transfer");
        var toUser = memberService.assertIsCommitteeMember(request.committee(), request.toUserId(), "Receive Transfer");

        Transaction fromTxn = toTxn.getLinked();
        fromTxn.setAmount(request.amount()*-1);
        fromTxn.setDate(request.date());
        fromTxn.setTxnMode(request.txnMode());
        fromTxn.setTxnUser(fromUser);
        fromTxn.setLogUser(logUser);
        fromTxn.setDescription("Transferred to " + toUser.getUserInfo());

        toTxn.setAmount(request.amount());
        toTxn.setDate(request.date());
        toTxn.setTxnMode(request.txnMode());
        toTxn.setTxnUser(toUser);
        toTxn.setLogUser(logUser);
        toTxn.setLinked(fromTxn);
        toTxn.setDescription("Received from " + fromUser.getUserInfo());

        transactionRepository.saveAll(List.of(toTxn, fromTxn));

        return TransactionMapper.toResponse(toTxn);
    }

    @Override
    public TransactionPageResponse getAll(Committee committee, TxnType txnType, Short year, Building building, DonationType donationType) {
        List<TransactionResponse> txns = transactionRepository.getTransfersByCommitteeAndYear(committee, year)
                .stream()
                .map(TransactionMapper::toResponse)
                .toList();
        return TransactionMapper.toPageResponse(txns);
    }

    @Override
    public TxnType getType() {
        return TxnType.TRANSFER;
    }
}
