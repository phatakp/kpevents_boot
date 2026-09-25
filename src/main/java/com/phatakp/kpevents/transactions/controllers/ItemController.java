package com.phatakp.kpevents.transactions.controllers;

import com.phatakp.kpevents.common.dto.ApiPageResponse;
import com.phatakp.kpevents.common.enums.ItemType;
import com.phatakp.kpevents.transactions.dto.request.ItemRequest;
import com.phatakp.kpevents.transactions.dto.response.ItemResponse;
import com.phatakp.kpevents.transactions.services.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{itemType}/{year}")
    public ResponseEntity<ApiPageResponse<ItemResponse>> getItems(
            @PathVariable ItemType itemType,
            @PathVariable short year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
            ) {
        Pageable pageable = PageRequest.of(page,size, Sort.by("itemName").ascending());
        Page<ItemResponse> items = itemService.getItems(itemType, year, pageable);
        return ResponseEntity.ok(ApiPageResponse.success(items));
    }

}
