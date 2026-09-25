package com.phatakp.kpevents.transactions.dto.response;

import com.phatakp.kpevents.common.enums.ItemType;
import com.phatakp.kpevents.transactions.entity.Item;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;

import java.io.Serializable;

/**
 * DTO for {@link com.phatakp.kpevents.transactions.entity.Item}
 */
@Builder
public record ItemResponse(
        Long id,
        String itemName,

        @Enumerated(EnumType.STRING)
        ItemType type,

        Float price,
        Float quantity,
        Float amount,
        Float availableQty,
        Float availableAmt) implements Serializable {

}