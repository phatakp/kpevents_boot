package com.phatakp.kpevents.transactions.services;

import com.phatakp.kpevents.common.enums.Committee;
import com.phatakp.kpevents.common.enums.ItemType;
import com.phatakp.kpevents.transactions.dto.response.ItemResponse;

import java.util.List;

public interface ItemService {
    List<ItemResponse> getItems(ItemType itemType, short year);
}
