package com.phatakp.kpevents.transactions.aspects;

import com.phatakp.kpevents.common.enums.Building;
import com.phatakp.kpevents.common.enums.Committee;
import com.phatakp.kpevents.common.enums.DonationType;
import com.phatakp.kpevents.common.enums.TxnType;
import com.phatakp.kpevents.transactions.dto.request.TransactionRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.*;

@Aspect
@Component
public class TransactionValidationAspect {
    private final Map<Building, Short> FLOORS_PER_BUILDING = new HashMap<>();
    private final Map<Committee, List<DonationType>> VALID_DONATION_TYPES_FOR_COMMITTEE = new HashMap<>();
    private final List<DonationType> VALID_DONATION_TYPES_FOR_BOOKINGS = new ArrayList<>();

    public TransactionValidationAspect() {
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

    @Before("@annotation(com.phatakp.kpevents.transactions.validators.ValidTxnRequest)")
    public void validateTransaction(JoinPoint joinPoint) throws MethodArgumentNotValidException {
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof TransactionRequest) {
                TransactionRequest request = (TransactionRequest) arg;

                switch (request.txnType()) {
                    case TxnType.DONATION -> isDonationRequestValid(request);
                    case TxnType.EXPENSE -> isExpenseRequestValid(request);
                    case TxnType.TRANSFER -> isTransferRequestValid(request);
                    default -> {
                        throwError(request,"txnType", "Invalid Txn Type: "+ request.txnType());
                    }
                };
            }
        }
    }

    private void isDonationRequestValid(TransactionRequest request) throws MethodArgumentNotValidException {
        assertValidDonationType(request);
        assertToUserAbsent(request);
        if (request.donationType().equals(DonationType.OTHER)) {
            assertDescPresent(request);
        } else {
            assertDescAbsent(request);
            assertDonorNamePresent(request);
            assertFlatNumberPresent(request);
        }

        if(VALID_DONATION_TYPES_FOR_BOOKINGS.contains(request.donationType()))
            assertBookingsPresent(request);
        else assertBookingsAbsent(request);
    }

    private void isExpenseRequestValid(TransactionRequest request) throws MethodArgumentNotValidException {
        assertDonationTypeAbsent(request);
        assertDescPresent(request);
        assertDonorNameAbsent(request);
        assertFlatNumberAbsent(request);
        assertToUserAbsent(request);
    }

    private void isTransferRequestValid(TransactionRequest request) throws MethodArgumentNotValidException {
        assertDonationTypeAbsent(request);
        assertDescAbsent(request);
        assertDonorNameAbsent(request);
        assertFlatNumberAbsent(request);
        assertToUserPresent(request);
    }

    void assertValidDonationType(TransactionRequest request) throws MethodArgumentNotValidException {
        if (request.txnType().equals(TxnType.DONATION) &&
                !VALID_DONATION_TYPES_FOR_COMMITTEE.get(request.committee())
                        .contains(request.donationType()))
        {
            throwError(request,"donationType",  "Invalid Donation Type: "+ request.donationType());
        }

    }


    void assertDonationTypeAbsent(TransactionRequest request) throws MethodArgumentNotValidException {
        if (request.donationType() != null) {
            throwError(request,"donationType",  "Invalid Donation Type: "+ request.donationType());
        }
    }

    void assertDescPresent(TransactionRequest request) throws MethodArgumentNotValidException {
        if (request.description() == null || request.description().isEmpty()) {
            throwError(request,"description",  "Invalid Description: "+ request.description());
        }
    }

    void assertDescAbsent(TransactionRequest request) throws MethodArgumentNotValidException {
        if (request.description() != null && !request.description().isEmpty()) {
            throwError(request,"description",  "Invalid Description: "+ request.description());
        }
    }

    void assertDonorNamePresent(TransactionRequest request) throws MethodArgumentNotValidException {
        if (request.donorName() == null || request.donorName().isEmpty()) {
            throwError(request,"donorName", "Invalid Donor Name: "+ request.donorName());
        }
    }

    void assertDonorNameAbsent(TransactionRequest request) throws MethodArgumentNotValidException {
        if (request.donorName() != null && !request.donorName().isEmpty()) {
           throwError(request, "donorName", "Invalid Donor Name: "+ request.donorName());
        }
    }

    void assertFlatNumberPresent(TransactionRequest request) throws MethodArgumentNotValidException {
        if (request.donorBuilding() == null || request.donorFlat()==null) {
            throwError(request,"donorFlat", "Invalid Flat Number: "+ request.donorBuilding() + request.donorFlat());
            return;
        }
        var floors = FLOORS_PER_BUILDING.get(request.donorBuilding());
        for (short i = 1; i <= floors; i++) {
            for (short j = 0; j <= 4; j++) {
                short flatNum = (short) (i * 100 + j);
                if (flatNum == request.donorFlat()) {
                    return;
                }
            }
        }
        throwError(request, "donorFlat", "Invalid Flat Number: "+ request.donorBuilding() + request.donorFlat());
    }

    void assertFlatNumberAbsent(TransactionRequest request) throws MethodArgumentNotValidException {
        if (request.donorBuilding() != null && request.donorFlat()!=null) {
            throwError(request,"donorFlat", "Invalid Flat Number: "+ request.donorBuilding() + request.donorFlat());
                    }
    }

    void assertToUserPresent(TransactionRequest request) throws MethodArgumentNotValidException {
        if (request.toUserId() == null || request.toUserId().isEmpty()) {
            throwError(request,"toUserId", "Invalid To User: "+ request.toUserId());
        }
    }

    void assertToUserAbsent(TransactionRequest request) throws MethodArgumentNotValidException {
        if (request.toUserId() != null && !request.toUserId().isEmpty()) {
            throwError(request, "toUserId", "Invalid To User: "+ request.toUserId());
        }
    }

    void assertBookingsPresent(TransactionRequest request) throws MethodArgumentNotValidException {
        if (request.bookings() == null || Objects.requireNonNull(request.bookings()).isEmpty())
            throwError(request, "bookings", "Invalid Bookings: "+ request.bookings());
    }

    void assertBookingsAbsent(TransactionRequest request) throws MethodArgumentNotValidException {
        if (request.bookings() != null  && !request.bookings().isEmpty())
            throwError(request, "bookings", "Bookings should be empty");
    }

    void throwError(TransactionRequest request, String field, String message) throws MethodArgumentNotValidException {
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "transactionRequest");
        bindingResult.rejectValue(field, "NotValid", message);
        // Throw standard exception so GlobalExceptionHandler handles it cleanly
        throw new MethodArgumentNotValidException(null, bindingResult);
    }
}
