package com.phatakp.kpevents.users.repos;

import com.phatakp.kpevents.common.enums.Committee;
import com.phatakp.kpevents.users.dto.response.UserBalance;
import com.phatakp.kpevents.users.dto.response.UserStats;
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

    @Query(value = """
    with
    committee_totals AS (
        select txn_user_id,committee, COALESCE(SUM(amount),0) AS total
        from transactions
        group by txn_user_id,transactions.committee
    ),
    year_totals AS (
        select txn_user_id,committee,year, COALESCE(SUM(amount),0) AS total
        from transactions
        group by txn_user_id,committee,year
    ),
    year_txn_type_totals AS (
        select txn_user_id,committee,year,txn_type, COALESCE(SUM(amount),0) AS total
        from transactions
        group by txn_user_id,committee,year,txn_type
    ),
    year_donation_type_totals AS (
        select txn_user_id,committee,year,type, COALESCE(SUM(amount),0) AS total
        from transactions
        join donations on transactions.id = donations.id
        group by txn_user_id,committee,year,type
    ),
    year_agg AS (
        select txn_user_id,
               committee,
               json_agg(json_build_object('year',year, 'total',total))::text AS data
        from year_totals
        group by txn_user_id,committee
    ),
    year_txn_type_agg AS (
        select txn_user_id,
               committee,
               json_agg(json_build_object('year',year, 'txnType',txn_type, 'total',total))::text AS data
        from year_txn_type_totals
        group by txn_user_id,committee
    ),
    year_donation_type_agg AS (
        select txn_user_id,
               committee,
               json_agg(json_build_object('year',year, 'donationType',type, 'total',total))::text AS data
        from year_donation_type_totals
        group by txn_user_id,committee
    )
    SELECT u.clerk_id, u.first_name, u.last_name,u.building,u.flat,
           ct.committee, ct.total,
           COALESCE(yg.data, '[]') AS balanceByYear,
           COALESCE(ytg.data, '[]') AS balanceByYearAndTxnType,
           COALESCE(ydg.data, '[]') AS balanceByYearAndDonationType
    from users u
    LEFT JOIN committee_totals ct ON u.clerk_id=ct.txn_user_id
    LEFT JOIN year_agg yg ON ct.txn_user_id=yg.txn_user_id and ct.committee=yg.committee
    LEFT JOIN year_txn_type_agg ytg ON ct.txn_user_id=ytg.txn_user_id and ct.committee=ytg.committee
    LEFT JOIN year_donation_type_agg ydg ON ct.txn_user_id=ydg.txn_user_id and ct.committee=ydg.committee
    """,nativeQuery = true)
    List<UserStats> getUsersStats();



    @Query(value = "select u from User u " +
            "left join fetch u.memberships m " +
            "where m.memberId.committee=:committee " +
            "order by u.firstName")
    List<User> getUsersByCommittee(Committee committee);

    @Query(value = "select u from User u " +
            "join fetch u.memberships m ")
    List<User> getAllUsersWithMembership();
}