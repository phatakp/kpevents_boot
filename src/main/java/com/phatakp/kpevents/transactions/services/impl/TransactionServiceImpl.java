package com.phatakp.kpevents.transactions.services.impl;

import com.phatakp.kpevents.common.enums.Building;
import com.phatakp.kpevents.common.enums.Committee;
import com.phatakp.kpevents.common.enums.DonationType;
import com.phatakp.kpevents.common.enums.TxnType;
import com.phatakp.kpevents.common.exceptions.ActionNotAllowedException;
import com.phatakp.kpevents.common.exceptions.BusinessRuleException;
import com.phatakp.kpevents.common.exceptions.ResourceNotFoundException;
import com.phatakp.kpevents.transactions.dto.request.TransactionRequest;
import com.phatakp.kpevents.transactions.dto.response.*;
import com.phatakp.kpevents.transactions.entity.Transaction;
import com.phatakp.kpevents.transactions.factory.TransactionTypeFactory;
import com.phatakp.kpevents.transactions.repos.TransactionRepository;
import com.phatakp.kpevents.transactions.services.TransactionService;
import com.phatakp.kpevents.transactions.strategies.transaction.TransactionTypeStrategy;
import com.phatakp.kpevents.transactions.validators.ValidTxnRequest;
import com.phatakp.kpevents.users.services.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    private final TransactionTypeFactory transactionTypeFactory;
    private final TransactionRepository transactionRepository;
    private final MemberService memberService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    @ValidTxnRequest
    public TransactionResponse createTransaction(TransactionRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (request.donationType()==null || (!request.donationType().equals(DonationType.ANNADAAN) &&
                !request.donationType().equals(DonationType.TEMPLE_ITEM)))
        {
            if (authentication == null) {
                throw new ActionNotAllowedException("Create transaction");
            }
            String clerkId = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
            memberService.assertIsCommitteeMember(request.committee(), clerkId, "Add Transaction");
        }

        TransactionTypeStrategy strategy = transactionTypeFactory.getStrategy(request.txnType());
        return strategy.process(request);
    }

    @Override
    public List<DonationStatsResponse> getDonationStatsByCommitteeAndYear(Committee committee, Short year) {
        return transactionRepository.getDonationStatsByCommitteeAndYear(committee.name(), year);
    }


    @Override
    public TransactionPageResponse getTransactions(Committee committee,
                                                   TxnType txnType,
                                                   Short year,
                                                   Building building,
                                                   DonationType donationType) {
        String clerkId = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        memberService.assertIsCommitteeMember(committee, clerkId, "Get Transactions");

        TransactionTypeStrategy strategy = transactionTypeFactory.getStrategy(txnType);
        return strategy.getAll(committee, txnType, year, building, donationType);

    }


    @Override
    public List<CommitteeBalanceResponse> getBalancesByCommittee(Committee committee) {
        return transactionRepository.getBalancesByCommittee(committee.name());
    }

    @Override
    public LinkedTransfer getLinkedTransfer(String txnId) {
        Transaction txn = transactionRepository.findById(txnId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Transaction", txnId));
        log.info("linked transfer fromUser: {}, toUser:{}", txn.getLinked().getTxnUser().getClerkId(), txn.getTxnUser().getClerkId());
        return new LinkedTransfer(txn.getId(), txn.getLinked().getTxnUser().getClerkId(), txn.getTxnUser().getClerkId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTransaction(String txnId) {
        Transaction txn = transactionRepository.findById(txnId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Transaction", txnId));
        if (txn.getTxnType().equals(TxnType.TRANSFER)) {
            Transaction fromTxn = txn.getLinked();
            transactionRepository.deleteAll(List.of(fromTxn, txn));
            return;
        }
        transactionRepository.delete(txn);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    @ValidTxnRequest
    public TransactionResponse updateTransaction(String txnId, TransactionRequest request) {
        try {
            String clerkId = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
            memberService.assertIsCommitteeMember(request.committee(), clerkId, "Update Transaction");

            Transaction txn = transactionRepository.findById(txnId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Transaction", txnId));
            if (!txn.getTxnType().equals(request.txnType()))
                throw new BusinessRuleException("INVALID_TXN_TYPE", "Transaction type cannot be changed");


            TransactionTypeStrategy strategy = transactionTypeFactory.getStrategy(request.txnType());
            return strategy.update(txn, request);
        } catch (Exception e) {
            log.error("Error updating transaction", e);
            throw new RuntimeException(e);
        }
    }

}
