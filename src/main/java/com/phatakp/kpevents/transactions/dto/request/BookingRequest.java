package com.phatakp.kpevents.transactions.dto.request;

import com.phatakp.kpevents.common.enums.ItemType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;

public record BookingRequest(
        @NotNull(message = "Item ID is required")
        Long itemId,

        @NotNull(message = "Item type is required")
        @Enumerated(EnumType.STRING)
        ItemType itemType,

        @NotNull(message = "Booking quantity is required")
        Float bookingQty,

        @NotNull(message = "Booking amount is required")
        Float bookingAmt
) {
}
