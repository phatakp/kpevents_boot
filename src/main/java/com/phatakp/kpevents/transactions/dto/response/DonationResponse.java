package com.phatakp.kpevents.transactions.dto.response;

import com.phatakp.kpevents.common.enums.Building;
import com.phatakp.kpevents.common.enums.DonationType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;

import java.util.List;

@Builder
public record DonationResponse(
        @Enumerated(EnumType.STRING)
        DonationType type,

        String donorName,

        @Enumerated(EnumType.STRING)
        Building building,

        Short flat,
        Short quantity,
        List<ItemBookingResponse> bookings
) {
}
