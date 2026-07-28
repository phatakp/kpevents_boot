package com.phatakp.kpevents.users.services.impl;

import com.phatakp.kpevents.common.exceptions.ResourceNotFoundException;
import com.phatakp.kpevents.users.dto.request.UserCreateRequest;
import com.phatakp.kpevents.users.dto.response.UserBalance;
import com.phatakp.kpevents.users.dto.response.UserResponse;
import com.phatakp.kpevents.users.entity.User;
import com.phatakp.kpevents.users.mappers.UserMapper;
import com.phatakp.kpevents.users.repos.UserRepository;
import com.phatakp.kpevents.users.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {

        User user = userRepository.findByClerkIdBeforeOrEmail(request.clerkId(),request.email()).orElse(null);
        if (user != null) {
            return updateUser(request);
        }
        User newUser = UserMapper.toEntity(request);
        newUser.setMemberships(List.of());
        User savedUser = userRepository.save(newUser);
        return UserMapper.toResponse(savedUser);

    }

    @Override
    public UserResponse updateUser(UserCreateRequest request) {
        User user = getUserById(request.clerkId());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setBuilding(request.flatNumber().building());
        user.setFlat(request.flatNumber().flat());
        User savedUser = userRepository.save(user);
        return UserMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse getProfile() {
        String clerkId = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User user = getUserById(clerkId);
        return UserMapper.toResponse(user);
    }

    @Override
    public List<UserBalance> getAllUserBalances() {
        return userRepository.getAllUserBalances();
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }


}
