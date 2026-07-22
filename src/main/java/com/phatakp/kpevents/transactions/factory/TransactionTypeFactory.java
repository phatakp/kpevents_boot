package com.phatakp.kpevents.transactions.factory;

import com.phatakp.kpevents.common.enums.TxnType;
import com.phatakp.kpevents.common.exceptions.BusinessRuleException;
import com.phatakp.kpevents.transactions.strategies.transaction.TransactionTypeStrategy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class TransactionTypeFactory {

    private final Map<TxnType, TransactionTypeStrategy> strategies = new HashMap<>();

    public TransactionTypeFactory(List<TransactionTypeStrategy> strategyList) {
        for (TransactionTypeStrategy strategy : strategyList) {
            strategies.put(strategy.getType(), strategy);
        }
    }

    public TransactionTypeStrategy getStrategy(TxnType txnType) {
        return Optional.ofNullable(strategies.get(txnType))
                .orElseThrow(() -> new BusinessRuleException("INVALID_TXN_TYPE","Invalid Transaction Type:"+txnType));
    }
}
