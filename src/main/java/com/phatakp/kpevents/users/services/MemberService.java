package com.phatakp.kpevents.users.services;

import com.phatakp.kpevents.common.enums.Committee;
import com.phatakp.kpevents.users.dto.request.MemberRequest;
import com.phatakp.kpevents.users.dto.response.UserResponse;
import com.phatakp.kpevents.users.entity.User;

import java.util.List;

public interface MemberService {
    List<UserResponse> getMembersByCommittee(Committee committeeName);

    List<UserResponse> getAllMembers();

    void becomeMember(Committee committeeName);

    void approveMember(MemberRequest request);

    void deleteMember(MemberRequest request);

    User assertIsCommitteeMember(Committee committeeName, String userId, String action);

}
