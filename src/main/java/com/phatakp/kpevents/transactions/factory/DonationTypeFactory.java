package com.phatakp.kpevents.transactions.factory;

import com.phatakp.kpevents.common.enums.DonationType;
import com.phatakp.kpevents.common.exceptions.BusinessRuleException;
import com.phatakp.kpevents.transactions.strategies.donation.DonationTypeStrategy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class DonationTypeFactory {

    private final Map<DonationType, DonationTypeStrategy> strategies = new HashMap<>();

    public DonationTypeFactory(List<DonationTypeStrategy> strategyList) {
        for (DonationTypeStrategy strategy : strategyList) {
            strategies.put(strategy.getDonationType(), strategy);
        }
    }

    public DonationTypeStrategy getStrategy(DonationType donationType) {
        return Optional.ofNullable(strategies.get(donationType))
                .orElseThrow(() -> new BusinessRuleException("INVALID_DONATION_TYPE","Invalid Donation Type:"+donationType));
    }
}
