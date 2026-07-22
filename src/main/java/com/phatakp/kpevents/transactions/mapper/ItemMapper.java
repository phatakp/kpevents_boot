package com.phatakp.kpevents.transactions.mapper;

import com.phatakp.kpevents.transactions.dto.response.ItemResponse;
import com.phatakp.kpevents.transactions.entity.Item;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper {
    public static ItemResponse toResponse(Item item, short year){
        return ItemResponse.builder()
                .id(item.getId())
                .itemName(item.getItemName())
                .type(item.getType())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .amount(item.getAmount())
                .availableQty(item.getAvailableQty(year))
                .availableAmt(item.getAvailableAmt(year))
                .build();
    }
}
