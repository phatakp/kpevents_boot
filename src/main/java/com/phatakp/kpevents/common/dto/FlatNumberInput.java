package com.phatakp.kpevents.common.dto;

import com.phatakp.kpevents.common.enums.Building;
import com.phatakp.kpevents.common.validators.ValidFlatNumber;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;

@Embeddable
@ValidFlatNumber
public record FlatNumberInput(
        @NotNull(message = "Building is required")
        @Enumerated(EnumType.STRING)
        Building building,

        @NotNull(message = "Flat Number is required")
        short flat
) {
}
