package com.phatakp.kpevents.transactions.strategies.transaction;

import com.phatakp.kpevents.common.enums.Building;
import com.phatakp.kpevents.common.enums.Committee;
import com.phatakp.kpevents.common.enums.DonationType;
import com.phatakp.kpevents.common.enums.TxnType;
import com.phatakp.kpevents.common.exceptions.BusinessRuleException;
import com.phatakp.kpevents.transactions.dto.request.TransactionRequest;
import com.phatakp.kpevents.transactions.dto.response.TransactionPageResponse;
import com.phatakp.kpevents.transactions.dto.response.TransactionResponse;
import com.phatakp.kpevents.transactions.entity.Donation;
import com.phatakp.kpevents.transactions.entity.Transaction;
import com.phatakp.kpevents.transactions.factory.DonationTypeFactory;
import com.phatakp.kpevents.transactions.mapper.TransactionMapper;
import com.phatakp.kpevents.transactions.repos.TransactionRepository;
import com.phatakp.kpevents.transactions.strategies.donation.DonationTypeStrategy;
import com.phatakp.kpevents.users.services.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DonationTxnStrategy implements TransactionTypeStrategy {

    private final MemberService memberService;
    private final DonationTypeFactory donationTypeFactory;
    private final TransactionRepository transactionRepository;


    @Override
    public TransactionResponse process(TransactionRequest request) {
        String userId = request.txnUserId();
        if (!request.donationType().equals(DonationType.ANNADAAN) &&
                !request.donationType().equals(DonationType.TEMPLE_ITEM)){
            userId = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        }
        var logUser = memberService.assertIsCommitteeMember(request.committee(), userId, "Add Donation");
        var txnUser = memberService.assertIsCommitteeMember(request.committee(), request.txnUserId(), "Receive Donation");

        DonationTypeStrategy strategy = donationTypeFactory.getStrategy(request.donationType());
        Donation donation = strategy.createDonation(request);

        Transaction transaction = TransactionMapper.toEntity(request);
        transaction.setTxnUser(txnUser);
        transaction.setLogUser(logUser);
        transaction.setDonation(donation);

        transaction = transactionRepository.save(transaction);
        return TransactionMapper.toResponse(transaction);

    }

    @Override
    public TransactionResponse update(Transaction txn, TransactionRequest request) {
        String userId = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        var logUser = memberService.assertIsCommitteeMember(request.committee(), userId, "Update Donation");
        var txnUser = memberService.assertIsCommitteeMember(request.committee(), request.txnUserId(), "Receive Donation");
        txn.setTxnUser(txnUser);
        txn.setLogUser(logUser);
        txn.setDescription(request.description());
        txn.setDate(request.date());
        txn.setTxnMode(request.txnMode());
        txn.setAmount(request.amount());

        if (!txn.getDonation().getType().equals(request.donationType()))
            throw new BusinessRuleException("INVALID_DONATION_TYPE","Donation type cannot be changed");

        DonationTypeStrategy strategy = donationTypeFactory.getStrategy(request.donationType());
        Donation donation = strategy.updateDonation(txn,request);
        txn.setDonation(donation);


        txn = transactionRepository.save(txn);
        return TransactionMapper.toResponse(txn);
    }

    @Override
    public TransactionPageResponse getAll(Committee committee, TxnType txnType, Short year, Building building, DonationType donationType) {
        DonationTypeStrategy strategy = donationTypeFactory.getStrategy(
                donationType == null
                        ? committee.equals(Committee.CULTURAL)
                          ? DonationType.CULTURAL
                          : DonationType.TEMPLE
                        : donationType);
        return strategy.getDonations(committee, year, building, donationType);
    }

    @Override
    public TxnType getType() {
        return TxnType.DONATION;
    }



}
