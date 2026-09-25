package com.phatakp.kpevents.transactions.services;


import com.phatakp.kpevents.common.enums.Committee;
import com.phatakp.kpevents.transactions.dto.request.TransactionQueryOptions;
import com.phatakp.kpevents.transactions.dto.request.TransactionRequest;
import com.phatakp.kpevents.transactions.dto.response.CommitteeStats;
import com.phatakp.kpevents.transactions.dto.response.DonationStatsResponse;
import com.phatakp.kpevents.transactions.dto.response.LinkedTransfer;
import com.phatakp.kpevents.transactions.dto.response.TransactionResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransactionService {
    TransactionResponse createTransaction(TransactionRequest request);

    List<DonationStatsResponse> getDonationStatsByCommitteeAndYear(Committee committee, Short year);

    Page<TransactionResponse> getTransactions(TransactionQueryOptions options);

    CommitteeStats getBalancesByCommittee(Committee committee);

    LinkedTransfer getLinkedTransfer(String txnId);

    void deleteTransaction(String txnId);

    TransactionResponse updateTransaction(String txnId, @Valid TransactionRequest request);
}
