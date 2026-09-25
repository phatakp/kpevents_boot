package com.phatakp.kpevents.transactions.services.impl;

import com.phatakp.kpevents.common.enums.Building;
import com.phatakp.kpevents.common.enums.Committee;
import com.phatakp.kpevents.common.enums.DonationType;
import com.phatakp.kpevents.common.enums.TxnType;
import com.phatakp.kpevents.common.exceptions.ActionNotAllowedException;
import com.phatakp.kpevents.common.exceptions.BusinessRuleException;
import com.phatakp.kpevents.common.exceptions.ResourceNotFoundException;
import com.phatakp.kpevents.transactions.dto.request.TransactionQueryOptions;
import com.phatakp.kpevents.transactions.dto.request.TransactionRequest;
import com.phatakp.kpevents.transactions.dto.response.CommitteeStats;
import com.phatakp.kpevents.transactions.dto.response.DonationStatsResponse;
import com.phatakp.kpevents.transactions.dto.response.LinkedTransfer;
import com.phatakp.kpevents.transactions.dto.response.TransactionResponse;
import com.phatakp.kpevents.transactions.entity.Transaction;
import com.phatakp.kpevents.transactions.factory.TransactionTypeFactory;
import com.phatakp.kpevents.transactions.repos.TransactionRepository;
import com.phatakp.kpevents.transactions.services.TransactionService;
import com.phatakp.kpevents.transactions.strategies.transaction.TransactionTypeStrategy;
import com.phatakp.kpevents.transactions.validators.ValidTxnRequest;
import com.phatakp.kpevents.users.services.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        if (request.donationType() == null || (!request.donationType().equals(DonationType.ANNADAAN) &&
                !request.donationType().equals(DonationType.TEMPLE_ITEM))) {
            if (authentication == null) {
                throw new ActionNotAllowedException("Create transaction");
            }
            assertIsCommitteeMember(request.committee(),"Add Transaction");
        }

        TransactionTypeStrategy strategy = transactionTypeFactory.getStrategy(request.txnType());
        return strategy.process(request);
    }

    @Override
    public List<DonationStatsResponse> getDonationStatsByCommitteeAndYear(Committee committee, Short year) {
        return transactionRepository.getDonationStatsByCommitteeAndYear(committee.name(), year);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getTransactions(TransactionQueryOptions options) {
        assertIsCommitteeMember(options.committee(),"Get Transactions");
        Sort sort = Sort.by("updatedAt").descending();

        if (!options.txnType().equals(TxnType.DONATION)) {
            sort = Sort.by("date").descending();
        }
        if (options.donationType() == null) {
            sort = Sort.by("d.donorBuilding").ascending().and(Sort.by("d.donorFlat").ascending());
        }

        Pageable pageable = PageRequest.of(options.page(), options.size(), sort);
        DonationType donationType = isDonation(options) ?
                options.committee().equals(Committee.CULTURAL) ? DonationType.CULTURAL : DonationType.TEMPLE
                : options.donationType();
        Building building = isDonation(options) ?
                options.building()==null ? Building.A : options.building() : null;
        String userName = options.txnType().equals(TxnType.TRANSFER) ? options.userName() : null;
        return transactionRepository.getFilteredTransactions(
                options.committee(),
                options.txnType(),
                options.year(),
                building,
                donationType,
                options.txnMode(),
                options.txnUserId(),
                userName,
                options.searchTerm(),
                pageable
        ).map(TransactionResponse::fromEntity);

    }


    @Override
    public CommitteeStats getBalancesByCommittee(Committee committee) {
        return transactionRepository.getBalancesByCommittee(committee.name()).stream().findFirst().orElse(null);
    }

    @Override
    public LinkedTransfer getLinkedTransfer(String txnId) {
        Transaction txn = assertTxnPresent(txnId);
        return new LinkedTransfer(txn.getId(), txn.getLinked().getTxnUser().getClerkId(), txn.getTxnUser().getClerkId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTransaction(String txnId) {
        Transaction txn = assertTxnPresent(txnId);
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
            assertIsCommitteeMember(request.committee(),"Update Transaction");
            Transaction txn = assertTxnPresent(txnId);
            assertTxnTypeIsSame(request.txnType(), txn.getTxnType());
            TransactionTypeStrategy strategy = transactionTypeFactory.getStrategy(request.txnType());
            return strategy.update(txn, request);
        } catch (Exception e) {
            log.error("Error updating transaction", e);
            throw new RuntimeException(e);
        }
    }

    private static void assertTxnTypeIsSame(TxnType requestType, TxnType txnType) {
        if (!txnType.equals(requestType))
            throw new BusinessRuleException("INVALID_TXN_TYPE", "Transaction type cannot be changed");
    }

    private void assertIsCommitteeMember(Committee committee, String action){
        String clerkId = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        memberService.assertIsCommitteeMember(committee, clerkId, action);
    }

    private Transaction assertTxnPresent(String txnId){
        return transactionRepository.findById(txnId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Transaction", txnId));
    }

    private boolean isDonation(TransactionQueryOptions options){
        return options.txnType().equals(TxnType.DONATION) && options.donationType() == null;
    }
}
