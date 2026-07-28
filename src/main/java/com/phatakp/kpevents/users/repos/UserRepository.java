package com.phatakp.kpevents.users.repos;

import com.phatakp.kpevents.common.enums.Committee;
import com.phatakp.kpevents.users.dto.response.UserBalance;
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

    @Query(value="select u.clerk_id,u.first_name,u.last_name," +
            "       u.building, u.flat," +
            "       t.committee, t.year, t.txn_type, d.type, sum(t.amount) balance" +
            "       from users u" +
            "            join committee_members m on m.user_id=u.clerk_id" +
            "            join transactions t on t.txn_user_id=u.clerk_id" +
            "            left join donations d on d.id=t.id" +
            "            where m.is_active=true" +
            "            and t.committee=m.committee " +
            "group by u.clerk_id,u.first_name,u.last_name," +
            "         u.building, u.flat," +
            "         t.committee, t.year, t.txn_type, d.type",nativeQuery = true)
    List<UserBalance> getAllUserBalances();



    @Query(value = "select u from User u " +
            "left join fetch u.memberships m " +
            "where m.memberId.committee=:committee")
    List<User> getUsersByCommittee(Committee committee);

    @Query(value = "select u from User u " +
            "join fetch u.memberships m ")
    List<User> getAllUsersWithMembership();
}