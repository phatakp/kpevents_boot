package com.phatakp.kpevents.transactions.controllers;

import com.phatakp.kpevents.common.dto.ApiPageResponse;
import com.phatakp.kpevents.common.enums.*;
import com.phatakp.kpevents.transactions.dto.request.TransactionQueryOptions;
import com.phatakp.kpevents.transactions.dto.request.TransactionRequest;
import com.phatakp.kpevents.transactions.dto.response.*;
import com.phatakp.kpevents.transactions.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.createTransaction(request));
    }

    @PutMapping("/{txnId}")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable String txnId,
            @Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.updateTransaction(txnId,request));
    }


    @GetMapping("/balances/committee/{committee}")
    public ResponseEntity<CommitteeStats> getBalancesByCommmittee(
            @PathVariable Committee committee) {
        return ResponseEntity.ok(transactionService.getBalancesByCommittee(committee));
    }


    @GetMapping("/donation/stats/{committee}/{year}")
    public ResponseEntity<List<DonationStatsResponse>> getDonationStats(
            @PathVariable Committee committee,
            @PathVariable Short year) {
        return ResponseEntity.ok(transactionService.getDonationStatsByCommitteeAndYear(committee, year));
    }



    @GetMapping("/committee/{committee}/{txnType}/{year}")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public ResponseEntity<ApiPageResponse<TransactionResponse>> getTransactions(
            @PathVariable Committee committee,
            @PathVariable TxnType txnType,
            @PathVariable Short year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String building,
            @RequestParam(required = false) DonationType donationType,
            @RequestParam(required = false) TxnMode txnMode,
            @RequestParam(required = false) String txnUserId,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String searchTerm
            ) {
        TransactionQueryOptions options = TransactionQueryOptions.builder()
                .committee(committee)
                .txnType(txnType)
                .year(year)
                .building(Building.fromChar(building))
                .donationType(donationType)
                .txnMode(txnMode)
                .txnUserId(txnUserId)
                .userName(userName)
                .searchTerm(searchTerm)
                .page(page)
                .size(size)
                .build();
        Page<TransactionResponse> txnPage = transactionService.getTransactions(options);
        return ResponseEntity.ok(ApiPageResponse.success(txnPage));
    }

    @GetMapping("/linked/{txnId}")
    public ResponseEntity<LinkedTransfer> getLinkedTransfer(@PathVariable String txnId) {
        return ResponseEntity.ok(transactionService.getLinkedTransfer(txnId));
    }

}
