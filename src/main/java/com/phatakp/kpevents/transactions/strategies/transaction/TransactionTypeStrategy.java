package com.phatakp.kpevents.transactions.strategies.transaction;

import com.phatakp.kpevents.common.enums.Building;
import com.phatakp.kpevents.common.enums.Committee;
import com.phatakp.kpevents.common.enums.DonationType;
import com.phatakp.kpevents.common.enums.TxnType;
import com.phatakp.kpevents.transactions.dto.request.TransactionRequest;
import com.phatakp.kpevents.transactions.dto.response.TransactionPageResponse;
import com.phatakp.kpevents.transactions.dto.response.TransactionResponse;
import com.phatakp.kpevents.transactions.entity.Transaction;

public interface TransactionTypeStrategy {
    TransactionResponse process(TransactionRequest txnRequest);
    TransactionResponse update(Transaction transaction, TransactionRequest txnRequest);
    TransactionPageResponse getAll(Committee committee,
                                   TxnType txnType,
                                   Short year,
                                   Building building,
                                   DonationType donationType);
    TxnType getType();
}
