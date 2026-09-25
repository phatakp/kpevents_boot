package com.phatakp.kpevents.transactions.strategies.donation;

import com.phatakp.kpevents.common.enums.DonationType;
import com.phatakp.kpevents.transactions.dto.request.TransactionRequest;
import com.phatakp.kpevents.transactions.entity.Donation;
import com.phatakp.kpevents.transactions.entity.Transaction;

public interface DonationTypeStrategy {
    Donation createDonation(TransactionRequest request);

    Donation updateDonation(Transaction txn, TransactionRequest request);

    DonationType getDonationType();
}
