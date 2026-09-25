package com.phatakp.kpevents.transactions.services;

import com.phatakp.kpevents.common.enums.ItemType;
import com.phatakp.kpevents.transactions.dto.request.ItemRequest;
import com.phatakp.kpevents.transactions.dto.response.ItemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemService {
    Page<ItemResponse> getItems(ItemType itemType, short year, Pageable pageable);

    Page<ItemResponse> getAnnadaanItems(Pageable pageable);
    ItemResponse createItem(ItemRequest request);
    ItemResponse updateItem(ItemRequest request, long itemId);
    void deleteItem(long itemId);
}
