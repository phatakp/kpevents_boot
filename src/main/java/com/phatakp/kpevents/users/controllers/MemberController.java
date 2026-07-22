package com.phatakp.kpevents.users.controllers;

import com.phatakp.kpevents.common.enums.Committee;
import com.phatakp.kpevents.users.dto.response.UserResponse;
import com.phatakp.kpevents.users.services.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/committee/{committeeName}")
    public ResponseEntity<List<UserResponse>> getMembersByCommittee(
            @PathVariable Committee committeeName
    ) {
        return ResponseEntity.ok(memberService.getMembersByCommittee(committeeName));
    }


    @PostMapping("/committee/{committeeName}")
    @Secured({"ROLE_ADMIN","ROLE_USER"})
    public ResponseEntity<Void> addCommitteeMember(
            @PathVariable Committee committeeName
    ) {
        memberService.becomeMember(committeeName);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
