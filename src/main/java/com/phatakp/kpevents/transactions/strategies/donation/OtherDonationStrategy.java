package com.phatakp.kpevents.transactions.strategies.donation;

import com.phatakp.kpevents.common.enums.DonationType;
import com.phatakp.kpevents.transactions.dto.request.TransactionRequest;
import com.phatakp.kpevents.transactions.entity.Donation;
import com.phatakp.kpevents.transactions.entity.Transaction;
import com.phatakp.kpevents.transactions.mapper.DonationMapper;
import com.phatakp.kpevents.transactions.repos.DonationRepository;
import com.phatakp.kpevents.transactions.repos.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OtherDonationStrategy implements DonationTypeStrategy {
    private final DonationRepository donationRepository;
    private final TransactionRepository transactionRepository;


    @Override
    public Donation createDonation(TransactionRequest request) {
        return DonationMapper.toEntity(request);
    }

    @Override
    public Donation updateDonation(Transaction txn, TransactionRequest request) {
        Donation donation = txn.getDonation();
        donation.setDonorBuilding(request.donorBuilding());
        donation.setDonorFlat(request.donorFlat());
        donation.setDonorName(request.donorName());
        donation.setTransaction(txn);
        return donationRepository.save(donation);
    }


    @Override
    public DonationType getDonationType() {
        return DonationType.OTHER;
    }


}
