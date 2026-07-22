package com.phatakp.kpevents.users.controllers;

import com.phatakp.kpevents.common.enums.Committee;
import com.phatakp.kpevents.users.dto.request.UserCreateRequest;
import com.phatakp.kpevents.users.dto.response.UserBalance;
import com.phatakp.kpevents.users.dto.response.UserResponse;
import com.phatakp.kpevents.users.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    @PreAuthorize("@userSecurity.isOwner(authentication, #request.clerkId())")
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody UserCreateRequest request
    ) throws BadRequestException {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PutMapping
    @PreAuthorize("@userSecurity.isOwner(authentication, #request.clerkId())")
    public ResponseEntity<UserResponse> updateUser(
            @Valid @RequestBody UserCreateRequest request
    ) {
        return ResponseEntity.ok(userService.updateUser(request));
    }


    @GetMapping("/me")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<UserResponse> getProfile() {
        return ResponseEntity.ok(userService.getProfile());
    }


    @GetMapping("/me/balances/committee/{committee}")
    @PreAuthorize("@userSecurity.isActiveMember(authentication, #committee)")
    public ResponseEntity<UserBalance> getCurrUserBalanceByCommittee(
            @PathVariable Committee committee
    ) {
        return ResponseEntity.ok(userService.getCurrUserBalancesByCommittee(committee));
    }

    @GetMapping("/balances/committee/{committee}")
    @PreAuthorize("@userSecurity.isActiveMember(authentication, #committee)")
    public ResponseEntity<List<UserBalance>> getMemberBalanceByCommittee(
            @PathVariable Committee committee
    ) {
        return ResponseEntity.ok(userService.getBalancesByCommittee(committee));
    }

}
