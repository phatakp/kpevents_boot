package com.phatakp.kpevents.users.services.impl;

import com.phatakp.kpevents.common.enums.Committee;
import com.phatakp.kpevents.common.exceptions.ActionNotAllowedException;
import com.phatakp.kpevents.common.exceptions.DuplicateResourceException;
import com.phatakp.kpevents.common.exceptions.ResourceNotFoundException;
import com.phatakp.kpevents.users.dto.request.MemberRequest;
import com.phatakp.kpevents.users.dto.response.UserResponse;
import com.phatakp.kpevents.users.entity.CommitteeMember;
import com.phatakp.kpevents.users.entity.CommitteeMemberId;
import com.phatakp.kpevents.users.entity.User;
import com.phatakp.kpevents.users.mappers.UserMapper;
import com.phatakp.kpevents.users.repos.CommitteeMemberRepository;
import com.phatakp.kpevents.users.repos.UserRepository;
import com.phatakp.kpevents.users.services.MemberService;
import com.phatakp.kpevents.users.services.UserService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final CommitteeMemberRepository memberRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public List<UserResponse> getMembersByCommittee(Committee committee) {
        return userRepository.getUsersByCommittee(committee).stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @Override
    public List<UserResponse> getAllMembers() {
        return userRepository.getAllUsersWithMembership().stream()
                .map(UserMapper::toResponse)
                .toList();
    }


    @Override
    @Transactional
    public void becomeMember(Committee committeeName) {
        String clerkId = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User user = userService.getUserById(clerkId);

        boolean exists = user.getMemberships()
                .stream()
                .anyMatch(m -> m.getMemberId().getCommittee().equals(committeeName));
        if (exists) {
            throw new DuplicateResourceException("Committee Member", committeeName);
        }
        CommitteeMemberId memberId = new CommitteeMemberId(committeeName, clerkId);
        CommitteeMember member = CommitteeMember.builder()
                .memberId(memberId)
                .user(user)
                .build();
        memberRepository.save(member);
    }

    @Override
    public void approveMember(MemberRequest request) {
        CommitteeMember member = getCommitteeMember(request.committee(), request.userId());
        member.setIsActive(true);
        memberRepository.save(member);
    }


    @Override
    public void deleteMember(MemberRequest request) {
        CommitteeMember member = getCommitteeMember(request.committee(), request.userId());
        memberRepository.delete(member);
    }

    @Override
    public User assertIsCommitteeMember(Committee committeeName, String userId, String action) {
        CommitteeMember member = getCommitteeMember(committeeName, userId);
        if (!member.getIsActive())
            throw new ActionNotAllowedException(action);
        return member.getUser();
    }


    private @NonNull CommitteeMember getCommitteeMember(Committee committee, String userId) {
        CommitteeMemberId memberId = new CommitteeMemberId(committee, userId);
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Committee Member", memberId));
    }
}
