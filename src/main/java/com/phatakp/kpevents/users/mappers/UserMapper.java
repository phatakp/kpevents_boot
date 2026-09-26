package com.phatakp.kpevents.users.mappers;

import com.phatakp.kpevents.common.enums.Committee;
import com.phatakp.kpevents.common.enums.MemberStatus;
import com.phatakp.kpevents.users.dto.request.UserCreateRequest;
import com.phatakp.kpevents.users.dto.response.ShortUser;
import com.phatakp.kpevents.users.dto.response.UserResponse;
import com.phatakp.kpevents.users.entity.CommitteeMember;
import com.phatakp.kpevents.users.entity.User;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserMapper {

    public static UserResponse toResponse(User user) {
        Map<Committee, MemberStatus> membership = new HashMap<>();
        for (CommitteeMember member : user.getMemberships()) {
            MemberStatus status = member.getIsActive() ? MemberStatus.ACTIVE_MEMBER : MemberStatus.INACTIVE_MEMBER;
            membership.put(member.getMemberId().getCommittee(), status);
        }
        membership.putIfAbsent(Committee.CULTURAL, MemberStatus.NON_MEMBER);
        membership.putIfAbsent(Committee.TEMPLE, MemberStatus.NON_MEMBER);

        return UserResponse.builder()
                .clerkId(user.getClerkId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .imageUrl(user.getImageUrl())
                .building(user.getBuilding())
                .flat(user.getFlat())
                .role(user.getRole())
                .membership(membership)
                .build();
    }

    public static ShortUser toShortUser(User user) {
        return ShortUser.builder()
                .clerkId(user.getClerkId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .building(user.getBuilding())
                .flat(user.getFlat())
                .build();
    }

    public static User toEntity(UserCreateRequest request) {
        return User.builder()
                .clerkId(request.clerkId())
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .imageUrl(request.imageUrl())
                .building(request.flatNumber().building())
                .flat(request.flatNumber().flat())
                .build();
    }


}
