package com.phatakp.kpevents.users.mappers;

import com.phatakp.kpevents.users.dto.response.UserMembership;
import com.phatakp.kpevents.users.entity.CommitteeMember;
import org.springframework.stereotype.Component;

@Component
public class CommiteeMemberMapper {

    public static UserMembership toMembership(CommitteeMember member) {
        if (member==null) return null;
        return UserMembership.builder()
                .committee(member.getMemberId().getCommittee())
                .isActive(member.getIsActive())
                .build();
    }
}
