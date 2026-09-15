package com.phatakp.kpevents.transactions.services;

import com.phatakp.kpevents.common.enums.ItemType;
import com.phatakp.kpevents.transactions.dto.request.ItemRequest;
import com.phatakp.kpevents.transactions.dto.response.ItemResponse;

import java.util.List;

public interface ItemService {
    List<ItemResponse> getItems(ItemType itemType, short year);

    List<ItemResponse> getAnnadaanItems();
    ItemResponse createItem(ItemRequest request);
    ItemResponse updateItem(ItemRequest request, long itemId);
    void deleteItem(long itemId);
}
