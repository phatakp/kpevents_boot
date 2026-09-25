package com.phatakp.kpevents.transactions.dto.response;

import com.phatakp.kpevents.common.enums.ItemType;

public interface ItemProjection {
    Long getId();
    String getItemName();
    ItemType getType();
    Float getPrice();
    Float getQuantity();
    Float getAmount();
    Float getAvailableQty();
    Float getAvailableAmt();

    default ItemResponse toResponse() {
        return new ItemResponse(
                getId(),
                getItemName(),
                getType(),
                getPrice(),
                getQuantity(),
                getAmount(),
                getAvailableQty(),
                getAvailableAmt()
        );
    }
}
