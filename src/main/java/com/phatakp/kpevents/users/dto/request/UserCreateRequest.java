package com.phatakp.kpevents.users.dto.request;

import com.phatakp.kpevents.common.dto.FlatNumberInput;
import com.phatakp.kpevents.common.validators.ValidFlatNumber;
import jakarta.persistence.Embedded;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.io.Serializable;

/**
 * DTO for {@link com.phatakp.kpevents.users.entity.User}
 */

public record UserCreateRequest (
        @NotBlank(message = "Clerk ID is required")
        @Pattern(regexp = "^user_.*$", message = "Invalid Clerk Id")
        String clerkId,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "First name is required")
        String firstName,

        String lastName,

        String imageUrl,

        @Embedded
        @Valid
        FlatNumberInput flatNumber

)

        implements Serializable {
}