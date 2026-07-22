package com.phatakp.kpevents.transactions.dto.request;

import com.phatakp.kpevents.common.enums.*;
import com.phatakp.kpevents.transactions.validators.ValidTxnRequest;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link com.phatakp.kpevents.transactions.entity.Transaction}
 */
//@ValidTxnRequest
public record TransactionRequest(

        @NotNull(message = "Amount is required")
        Float amount,

        @NotNull(message = "Date is required")
        @PastOrPresent(message = "Date must be in the past or present")
        LocalDate date,

        @NotNull(message = "Committee is required")
        @Enumerated(EnumType.STRING)
        Committee committee,

        @NotNull(message = "Year is required")
        @Min(value = 2025, message = "Year must be greater than or equal to 2025")
        Short year,

        @NotBlank(message = "Txn User Id is required")
        @Pattern(regexp = "^user_.*$", message = "Invalid Txn User Id")
        String txnUserId,

        @NotNull(message = "Txn Type is required")
        @Enumerated(EnumType.STRING)
        TxnType txnType,

        @NotNull(message = "Txn Mode is required")
        @Enumerated(EnumType.STRING)
        TxnMode txnMode,

        @Enumerated(EnumType.STRING)
        DonationType donationType,

        String description,
        String donorName,
        Building donorBuilding,
        Short donorFlat,
        Short donorQuantity,
        String toUserId,
        List<BookingRequest> bookings

) implements Serializable {
}