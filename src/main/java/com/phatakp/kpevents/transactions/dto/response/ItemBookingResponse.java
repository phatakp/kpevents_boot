package com.phatakp.kpevents.transactions.dto.response;

import com.phatakp.kpevents.common.enums.ItemType;
import lombok.Builder;

@Builder
public record ItemBookingResponse(
        Long itemId,
        String itemName,
        ItemType itemType,
        Float price,
        Float bookingQty,
        Float bookingAmt,
        Float totalQty,
        Float totalAmt
) {
}
