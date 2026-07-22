package com.phatakp.kpevents.users.repos;

import com.phatakp.kpevents.common.enums.Committee;
import com.phatakp.kpevents.users.entity.User;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String username);

    @Query(value = "select u from User u " +
            "left join fetch u.memberships " +
            "where u.clerkId = :id")
    Optional<User> findById(@NonNull String id);

    Optional<User> findByClerkIdBeforeOrEmail(String id, String email);

    @Query(value="select u from User u " +
            "left join fetch u.userTxns t " +
            "left join fetch t.donation ")
    List<User> getUserBalances();

    @Query(value="select u from User u " +
            "left join fetch u.userTxns t " +
            "left join fetch t.donation " +
            "where u.clerkId=:userId ")
    Optional<User> getCurrUserBalances(String userId);

    @Query(value = "select u from User u " +
            "left join fetch u.memberships m " +
            "where m.memberId.committee=:committee")
    List<User> getUsersByCommittee(Committee committee);

    @Query(value = "select u from User u " +
            "join fetch u.memberships m ")
    List<User> getAllUsersWithMembership();
}