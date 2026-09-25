package com.phatakp.kpevents.common.dto;

import org.springframework.data.domain.Page;
import java.util.List;

public record ApiPageResponse<T>(
        List<T> data,
        PageMeta meta
) {
    public static <T> ApiPageResponse<T> success(Page<T> page) {
        return new ApiPageResponse<>(
                page.getContent(),
                PageMeta.from(page)
        );
    }
}
