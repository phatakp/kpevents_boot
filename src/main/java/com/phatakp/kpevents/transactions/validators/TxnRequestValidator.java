package com.phatakp.kpevents.transactions.validators;

import com.phatakp.kpevents.common.enums.Building;
import com.phatakp.kpevents.common.enums.Committee;
import com.phatakp.kpevents.common.enums.DonationType;
import com.phatakp.kpevents.common.enums.TxnType;
import com.phatakp.kpevents.common.exceptions.BusinessRuleException;
import com.phatakp.kpevents.transactions.dto.request.TransactionRequest;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TxnRequestValidator {
    private final Map<Building, Short> FLOORS_PER_BUILDING = new HashMap<>();
    private final Map<Committee, List<DonationType>> VALID_DONATION_TYPES_FOR_COMMITTEE = new HashMap<>();
    private final List<DonationType> VALID_DONATION_TYPES_FOR_BOOKINGS = new ArrayList<>()  ;


    public TxnRequestValidator() {
        // Optional: Initialization logic if needed
        FLOORS_PER_BUILDING.put(Building.A, (short) 12);
        FLOORS_PER_BUILDING.put(Building.B, (short) 12);
        FLOORS_PER_BUILDING.put(Building.C, (short) 11);
        FLOORS_PER_BUILDING.put(Building.D, (short) 11);
        FLOORS_PER_BUILDING.put(Building.E, (short) 12);
        FLOORS_PER_BUILDING.put(Building.F, (short) 12);
        FLOORS_PER_BUILDING.put(Building.G, (short) 12);

        VALID_DONATION_TYPES_FOR_COMMITTEE.put(Committee.CULTURAL, List.of(DonationType.CULTURAL, DonationType.ANNADAAN, DonationType.OTHER));
        VALID_DONATION_TYPES_FOR_COMMITTEE.put(Committee.TEMPLE, List.of(DonationType.TEMPLE, DonationType.TEMPLE_ITEM, DonationType.OTHER));

        VALID_DONATION_TYPES_FOR_BOOKINGS.addAll(List.of(DonationType.TEMPLE_ITEM, DonationType.ANNADAAN));
    }

    public void isValid(TransactionRequest request) {
        switch (request.txnType()) {
            case TxnType.DONATION -> isDonationRequestValid(request);
            case TxnType.EXPENSE -> isExpenseRequestValid(request);
            case TxnType.TRANSFER -> isTransferRequestValid(request);
            default -> throw new BusinessRuleException("INVALID_TXN_TYPE","Invalid Transaction Type:"+ request.txnType());
        };
    }

    void isDonationRequestValid(TransactionRequest request) {
        if (assertValidDonationType(request) && assertToUserAbsent(request) && ((request.donationType().equals(DonationType.OTHER) && assertDescPresent(request)) ||
                (!request.donationType().equals(DonationType.OTHER) && assertDescAbsent(request))
        ) && (!request.donationType().equals(DonationType.OTHER) && assertDonorNamePresent(request) &&
                assertValidFlatNumber(request)
        )) {
            if ((!VALID_DONATION_TYPES_FOR_BOOKINGS.contains(request.donationType()) || !assertBookingsPresent(request))) {
                if (!VALID_DONATION_TYPES_FOR_BOOKINGS.contains(request.donationType())) {
                    assertBookingsAbsent(request);
                }
            }
        }
    }

    void isExpenseRequestValid(TransactionRequest request) {
        if (assertValidDonationType(request) && assertDescPresent(request) && assertDonorNameAbsent(request) && assertFlatNumberAbsent(request) && assertToUserAbsent(request)) {
            assertBookingsAbsent(request);
        }
    }

    void isTransferRequestValid(TransactionRequest request) {
        if (assertValidDonationType(request) && assertDescAbsent(request) && assertDonorNameAbsent(request) && assertFlatNumberAbsent(request) && assertToUserPresent(request)) {
            assertBookingsAbsent(request);
        }
    }

    boolean assertValidDonationType(TransactionRequest request) {
        if (
                (request.txnType().equals(TxnType.DONATION) &&
                        VALID_DONATION_TYPES_FOR_COMMITTEE.get(request.committee())
                                .contains(request.donationType())
                ) || (!request.txnType().equals(TxnType.DONATION) && request.donationType() == null)
        ) {
            return true;
        }
        throw new BusinessRuleException("INVALID_DONATION_TYPE","Invalid donation type: "+request.donationType());

    }

    boolean assertDescPresent(TransactionRequest request) {
        if (request.description() != null && !request.description().isEmpty())
            return true;
        throw new BusinessRuleException("INVALID_DESCRIPTION","Description is required");
    }

    boolean assertDescAbsent(TransactionRequest request) {
        if (request.description() == null || Objects.requireNonNull(request.description()).isEmpty())
            return true;
        throw new BusinessRuleException("INVALID_DESCRIPTION","Description should be empty");

    }

    boolean assertDonorNameAbsent(TransactionRequest request) {
        if (request.donorName() == null || Objects.requireNonNull(request.donorName()).isEmpty())
            return true;
        throw new BusinessRuleException("INVALID_DONOR_NAME","Donor Name should be empty");
    }

    boolean assertDonorNamePresent(TransactionRequest request) {
        if (request.donorName() != null && !request.donorName().isEmpty())
            return true;
        throw new BusinessRuleException("INVALID_DONOR_NAME","Donor Name is required");
    }

    boolean assertFlatNumberAbsent(TransactionRequest request) {
        if (request.donorFlat() == null && request.donorBuilding() == null)
            return true;
        throw new BusinessRuleException("INVALID_FLAT","Flat Number should be empty");
    }

    boolean assertValidFlatNumber(TransactionRequest request) {

        var floors = FLOORS_PER_BUILDING.get(request.donorBuilding());
        for (short i = 1; i <= floors; i++) {
            for (short j = 0; j <= 4; j++) {
                short flatNum = (short) (i * 100 + j);
                if (flatNum == request.donorFlat()) {
                    return true;
                }
            }
        }
        throw new BusinessRuleException("INVALID_FLAT","Invalid Flat Number: "+request.donorBuilding()+request.donorFlat());
    }

    boolean assertToUserPresent(TransactionRequest request) {
        if (request.toUserId() != null && !request.toUserId().isEmpty())
            return true;
        throw new BusinessRuleException("INVALID_TO_USER","To user is required");
    }

    boolean assertToUserAbsent(TransactionRequest request) {
        if (request.toUserId() == null || Objects.requireNonNull(request.toUserId()).isEmpty())
            return true;
        throw new BusinessRuleException("INVALID_TO_USER","To user should be empty");
    }

    boolean assertBookingsPresent(TransactionRequest request) {
        if (request.bookings() != null && !request.bookings().isEmpty())
            return true;
        throw new BusinessRuleException("INVALID_BOOKINGS","Item Bookings required");
    }

    boolean assertBookingsAbsent(TransactionRequest request) {
        if (request.bookings() == null || Objects.requireNonNull(request.bookings()).isEmpty())
            return true;
        throw new BusinessRuleException("INVALID_BOOKINGS","Item Bookings should be empty");
    }

}


