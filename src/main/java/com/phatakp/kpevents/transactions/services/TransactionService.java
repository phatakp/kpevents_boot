package com.phatakp.kpevents.transactions.services;


import com.phatakp.kpevents.common.enums.Building;
import com.phatakp.kpevents.common.enums.Committee;
import com.phatakp.kpevents.common.enums.DonationType;
import com.phatakp.kpevents.common.enums.TxnType;
import com.phatakp.kpevents.transactions.dto.request.TransactionRequest;
import com.phatakp.kpevents.transactions.dto.response.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransactionService {
    TransactionResponse createTransaction(TransactionRequest request);

    List<DonationStatsResponse> getDonationStatsByCommitteeAndYear(Committee committee, Short year);

    TransactionPageResponse getTransactions(Committee committee,
                                            TxnType txnType,
                                            Short year,
                                            Building building,
                                            DonationType donationType);

    CommitteeStats getBalancesByCommittee(Committee committee);

    LinkedTransfer getLinkedTransfer(String txnId);

    void deleteTransaction(String txnId);

    TransactionResponse updateTransaction(String txnId, @Valid TransactionRequest request);
}
