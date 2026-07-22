package com.phatakp.kpevents.transactions.dto.request;

import com.phatakp.kpevents.common.enums.ItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * DTO for {@link com.phatakp.kpevents.transactions.entity.Item}
 */

public record ItemRequest(
        @NotBlank(message = "Item name is required")
        String itemName,

        @NotNull(message = "Item type is required")
        ItemType type,

        Float price,
        Float quantity,
        Float amount
) implements Serializable {
}