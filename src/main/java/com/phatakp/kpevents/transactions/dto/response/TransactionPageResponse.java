package com.phatakp.kpevents.transactions.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record TransactionPageResponse(
        Long totalElements, Long totalPages, Double totalAmount,
        List<TransactionResponse> data
) {
}
