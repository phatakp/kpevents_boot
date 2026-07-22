package com.phatakp.kpevents.transactions.strategies.donation;

import com.phatakp.kpevents.common.enums.Building;
import com.phatakp.kpevents.common.enums.Committee;
import com.phatakp.kpevents.common.enums.DonationType;
import com.phatakp.kpevents.transactions.dto.request.TransactionRequest;
import com.phatakp.kpevents.transactions.dto.response.TransactionPageResponse;
import com.phatakp.kpevents.transactions.entity.Donation;
import com.phatakp.kpevents.transactions.entity.Transaction;

public interface DonationTypeStrategy {
    Donation createDonation(TransactionRequest request);
    Donation updateDonation(Transaction txn, TransactionRequest request);
    TransactionPageResponse getDonations(Committee committee,
                                         Short year,
                                         Building building,
                                         DonationType donationType);
    DonationType getDonationType();
}
