package com.phatakp.kpevents.users.utils;

import com.phatakp.kpevents.common.enums.Committee;
import com.phatakp.kpevents.common.exceptions.ActionNotAllowedException;
import com.phatakp.kpevents.users.entity.CommitteeMember;
import com.phatakp.kpevents.users.repos.CommitteeMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSecurity {
    private final CommitteeMemberRepository committeeMemberRepository;

    public boolean isOwner(Authentication authentication, String clerkId) {
        if (authentication == null || clerkId == null) {
            return false;
        }
        return authentication.getName().equals(clerkId);
    }

    public boolean isActiveMember(Authentication authentication, Committee committee) {
        if (authentication == null) {
            return false;
        }
        var member = committeeMemberRepository.isActiveMember(committee, authentication.getName())
                .orElse(null);
        return member!=null;
    }
}
