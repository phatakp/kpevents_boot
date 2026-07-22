package com.phatakp.kpevents.transactions.services.impl;

import com.phatakp.kpevents.common.enums.ItemType;
import com.phatakp.kpevents.transactions.dto.response.ItemResponse;
import com.phatakp.kpevents.transactions.mapper.ItemMapper;
import com.phatakp.kpevents.transactions.repos.ItemRepository;
import com.phatakp.kpevents.transactions.services.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Override
    public List<ItemResponse> getItems(ItemType itemType, short year) {
        return itemRepository.getItems(itemType).stream()
                .filter(item->item.getAvailableAmt(year)>0 || item.getAvailableQty(year)>0)
                .map(item -> ItemMapper.toResponse(item, year))
                .toList();
    }
}
