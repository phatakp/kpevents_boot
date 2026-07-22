package com.phatakp.kpevents.users.services;

import com.phatakp.kpevents.common.enums.Committee;
import com.phatakp.kpevents.users.dto.request.UserCreateRequest;
import com.phatakp.kpevents.users.dto.response.UserBalance;
import com.phatakp.kpevents.users.dto.response.UserResponse;
import com.phatakp.kpevents.users.entity.User;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserCreateRequest request) throws BadRequestException;

    UserResponse updateUser(UserCreateRequest request);

    UserResponse getProfile();

    List<UserBalance> getBalancesByCommittee(Committee committee);

    User getUserById(String userId);

    UserBalance getCurrUserBalancesByCommittee(Committee committee);
}
