package com.phatakp.kpevents.users.repos;

import com.phatakp.kpevents.common.enums.Committee;
import com.phatakp.kpevents.users.entity.CommitteeMember;
import com.phatakp.kpevents.users.entity.CommitteeMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CommitteeMemberRepository extends JpaRepository<CommitteeMember, CommitteeMemberId> {


    @Query(value="select m from CommitteeMember m " +
            "where m.memberId.committee = :committee " +
            "and m.memberId.userId = :clerkId " +
            "and m.isActive = true")
    Optional<CommitteeMember> isActiveMember(Committee committee, String clerkId);
}