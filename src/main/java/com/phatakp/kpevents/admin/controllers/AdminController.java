package com.phatakp.kpevents.admin.controllers;

import com.phatakp.kpevents.admin.entity.Config;
import com.phatakp.kpevents.admin.services.ConfigService;
import com.phatakp.kpevents.transactions.dto.request.ItemRequest;
import com.phatakp.kpevents.transactions.dto.response.ItemResponse;
import com.phatakp.kpevents.transactions.services.ItemService;
import com.phatakp.kpevents.transactions.services.TransactionService;
import com.phatakp.kpevents.users.dto.request.MemberRequest;
import com.phatakp.kpevents.users.dto.response.UserResponse;
import com.phatakp.kpevents.users.services.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MemberService memberService;
    private final ConfigService configService;
    private final TransactionService transactionService;
    private final ItemService itemService;

    @GetMapping("/config")
    public ResponseEntity<Config> getConfig() {
        return ResponseEntity.ok(configService.getConfig());
    }

    @PutMapping("/config")
    public ResponseEntity<Config> updateConfig(
            @Valid @RequestBody Config request
    ) {
        return ResponseEntity.ok(configService.updateConfig(request));
    }

    @GetMapping("/members")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<List<UserResponse>> getAllMembers() {
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    @PutMapping("/members/approve")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> approveMember(
            @Valid @RequestBody MemberRequest request) {
        memberService.approveMember(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/members/delete")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> deleteMember(
            @Valid @RequestBody MemberRequest request) {
        memberService.deleteMember(request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/transactions/{txnId}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable String txnId) {
        transactionService.deleteTransaction(txnId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/items")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<List<ItemResponse>> getAnnadaanItems() {
        return ResponseEntity.ok(itemService.getAnnadaanItems());
    }

    @PostMapping("/items")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<ItemResponse> createItem(
           @Valid @RequestBody ItemRequest request
    ) {
        return ResponseEntity.ok(itemService.createItem(request));
    }

    @PutMapping("/items/{itemId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<ItemResponse> updateItem(
            @Valid @RequestBody ItemRequest request,
            @PathVariable long itemId
    ) {
        return ResponseEntity.ok(itemService.updateItem(request,itemId));
    }

    @DeleteMapping("/items/{itemId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> deleteItem(
            @PathVariable long itemId) {
        itemService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }
}
