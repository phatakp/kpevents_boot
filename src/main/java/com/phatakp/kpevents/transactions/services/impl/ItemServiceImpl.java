package com.phatakp.kpevents.transactions.services.impl;

import com.phatakp.kpevents.common.enums.ItemType;
import com.phatakp.kpevents.common.exceptions.DuplicateResourceException;
import com.phatakp.kpevents.common.exceptions.ResourceNotFoundException;
import com.phatakp.kpevents.transactions.dto.request.ItemRequest;
import com.phatakp.kpevents.transactions.dto.response.ItemProjection;
import com.phatakp.kpevents.transactions.dto.response.ItemResponse;
import com.phatakp.kpevents.transactions.entity.Item;
import com.phatakp.kpevents.transactions.mapper.ItemMapper;
import com.phatakp.kpevents.transactions.repos.ItemRepository;
import com.phatakp.kpevents.transactions.services.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Override
    public Page<ItemResponse> getItems(ItemType itemType, short year, Pageable pageable) {
        return itemRepository.getAvailableItemsForYear(itemType.name(),year,pageable).map(ItemProjection::toResponse);
    }

    @Override
    public Page<ItemResponse> getAnnadaanItems(Pageable pageable) {
        return itemRepository.getAnnadaanItems(pageable).map(ItemProjection::toResponse);
    }

    @Override
    public ItemResponse createItem(ItemRequest request) {
        assertItemNotExists(request.itemName());
        Item item = ItemMapper.toEntity(request);
        Item savedItem = itemRepository.save(item);
        return ItemMapper.toResponse(savedItem);
    }

    @Override
    public ItemResponse updateItem(ItemRequest request, long itemId) {
        Item item = assertItemExists(itemId);
        Item namedItem = findByName(request.itemName());
        if (namedItem != null && !namedItem.getId().equals(itemId)) {
            throw new DuplicateResourceException("Item",request.itemName());
        }
        Item updatedItem = ItemMapper.updateEntity(request,item);
        Item savedItem = itemRepository.save(updatedItem);
        return ItemMapper.toResponse(savedItem);
    }

    @Override
    public void deleteItem(long itemId) {
        Item item = assertItemExists(itemId);
        itemRepository.delete(item);
    }

    private void assertItemNotExists(String itemName) {
        if (itemRepository.existsByItemName(itemName)) {
            throw new DuplicateResourceException("Item",itemName);
        }
    }

    private Item assertItemExists(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new ResourceNotFoundException("Item",itemId));
    }

    private Item findByName(String itemName) {
        return itemRepository.findItemByItemName(itemName).orElse(null);
    }
}
