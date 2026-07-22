package com.phatakp.kpevents.transactions.controllers;

import com.phatakp.kpevents.common.enums.ItemType;
import com.phatakp.kpevents.transactions.dto.response.ItemResponse;
import com.phatakp.kpevents.transactions.services.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{itemType}/{year}")
    public ResponseEntity<List<ItemResponse>> getItems(@PathVariable ItemType itemType,
                                                      @PathVariable short year) {
        return ResponseEntity.ok(itemService.getItems(itemType, year));
    }
}
