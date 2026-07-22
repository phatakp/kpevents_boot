package com.phatakp.kpevents.transactions.controllers;

import com.phatakp.kpevents.common.enums.Building;
import com.phatakp.kpevents.common.enums.Committee;
import com.phatakp.kpevents.common.enums.DonationType;
import com.phatakp.kpevents.common.enums.TxnType;
import com.phatakp.kpevents.transactions.dto.request.TransactionRequest;
import com.phatakp.kpevents.transactions.dto.response.*;
import com.phatakp.kpevents.transactions.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<CommitteeBalanceResponse>> getBalancesByCommmittee(
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
    public ResponseEntity<TransactionPageResponse> getTransactions(
            @PathVariable Committee committee,
            @PathVariable TxnType txnType,
            @PathVariable Short year,
            @RequestParam(required = false) Building building,
            @RequestParam(required = false) DonationType donationType) {
        return ResponseEntity.ok(transactionService.getTransactions(committee, txnType, year,building,donationType));
    }

    @GetMapping("/linked/{txnId}")
    public ResponseEntity<LinkedTransfer> getLinkedTransfer(@PathVariable String txnId) {
        return ResponseEntity.ok(transactionService.getLinkedTransfer(txnId));
    }

}
