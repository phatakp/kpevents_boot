package com.phatakp.kpevents.users.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSecurity {

    public boolean isOwner(Authentication authentication, String clerkId) {
        if (authentication == null || clerkId == null) {
            return false;
        }
        return authentication.getName().equals(clerkId);
    }


}
