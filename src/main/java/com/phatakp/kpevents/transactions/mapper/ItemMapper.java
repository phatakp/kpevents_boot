package com.phatakp.kpevents.transactions.mapper;

import com.phatakp.kpevents.transactions.dto.request.ItemRequest;
import com.phatakp.kpevents.transactions.dto.response.ItemResponse;
import com.phatakp.kpevents.transactions.entity.Item;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper {
    public static ItemResponse toResponse(Item item, short year) {
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

    public static ItemResponse toResponse(Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                .itemName(item.getItemName())
                .type(item.getType())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .amount(item.getAmount())
                .availableQty(item.getQuantity())
                .availableAmt(item.getAmount())
                .build();
    }

    public static Item toEntity(ItemRequest request) {
        return Item.builder()
                .itemName(request.itemName())
                .type(request.type())
                .price(request.price())
                .quantity(request.quantity())
                .amount(request.amount())
                .build();
    }

    public static Item updateEntity(ItemRequest request, Item item) {
        item.setItemName(request.itemName());
        item.setType(request.type());
        item.setPrice(request.price());
        item.setQuantity(request.quantity());
        item.setAmount(request.amount());
        return item;
    }
}
