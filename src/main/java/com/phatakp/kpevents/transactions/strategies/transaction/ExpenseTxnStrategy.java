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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ExpenseTxnStrategy implements TransactionTypeStrategy {

    private final TransactionRepository transactionRepository;
    private final MemberService memberService;

    @Override
    public TransactionResponse process(TransactionRequest request) {
        String userId = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        var logUser = memberService.assertIsCommitteeMember(request.committee(),userId,"Add Expense");
        var txnUser = memberService.assertIsCommitteeMember(request.committee(), request.txnUserId(), "Make Expense");

        Transaction transaction = TransactionMapper.toEntity(request);
        transaction.setAmount(request.amount()*-1);
        transaction.setTxnUser(txnUser);
        transaction.setLogUser(logUser);

        transaction = transactionRepository.save(transaction);
        return TransactionMapper.toResponse(transaction);
    }

    @Override
    public TransactionResponse update(Transaction txn, TransactionRequest request) {
        String userId = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        var logUser = memberService.assertIsCommitteeMember(request.committee(),userId,"Update Expense");
        var txnUser = memberService.assertIsCommitteeMember(request.committee(), request.txnUserId(), "Make Expense");

        txn.setDescription(request.description());
        txn.setAmount(request.amount()*-1);
        txn.setDate(request.date());
        txn.setTxnMode(request.txnMode());
        txn.setTxnUser(txnUser);
        txn.setLogUser(logUser);
        txn = transactionRepository.save(txn);
        return TransactionMapper.toResponse(txn);
    }

    @Override
    public TransactionPageResponse getAll(Committee committee, TxnType txnType, Short year, Building building, DonationType donationType) {
        List<TransactionResponse> txns = transactionRepository.getExpensesByCommitteeAndYear(committee, year)
                .stream()
                .map(TransactionMapper::toResponse)
                .toList();
        return TransactionMapper.toPageResponse(txns);
    }

    @Override
    public TxnType getType() {
        return TxnType.EXPENSE;
    }
}
