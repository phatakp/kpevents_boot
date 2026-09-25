package com.phatakp.kpevents.transactions.repos;

import com.phatakp.kpevents.common.enums.*;
import com.phatakp.kpevents.transactions.dto.response.CommitteeStats;
import com.phatakp.kpevents.transactions.dto.response.DonationStatsResponse;
import com.phatakp.kpevents.transactions.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {


    @Query(value = "SELECT t FROM Transaction t " +
            "JOIN FETCH t.txnUser " +
            "LEFT JOIN FETCH t.donation d " +
            "LEFT JOIN FETCH d.bookings b " +
            "LEFT JOIN FETCH b.item i " +
            "WHERE t.committee = :committee " +
            "AND ((:txnType IN ('EXPENSE','DONATION') AND t.txnType = :txnType) " +
            "  OR (:txnType = 'TRANSFER' AND t.txnType = :txnType AND t.amount > 0)) " +
            "AND t.year = :year " +
            "AND (:building IS NULL OR d.donorBuilding = :building) " +
            "AND (:donationType IS NULL OR d.type = :donationType) " +
            "AND (:txnMode IS NULL OR t.txnMode = :txnMode) " +
            "AND (:txnUserId IS NULL OR t.txnUser.clerkId = :txnUserId) " +
            "AND (:userName IS NULL OR LOWER(COALESCE(t.description, '')) LIKE LOWER(CONCAT('%', CAST(:userName AS string), '%'))) " +
            "AND (:searchTerm IS NULL OR (" +
            "    LOWER(COALESCE(t.description, '')) LIKE LOWER(CONCAT('%', CAST(:searchTerm AS string), '%')) " +
            "    OR LOWER(COALESCE(d.donorName, '')) LIKE LOWER(CONCAT('%', CAST(:searchTerm AS string), '%')) " +
            "    OR LOWER(CONCAT(" +
            "         COALESCE(d.donorBuilding, ''), " +
            "         COALESCE(CAST(d.donorFlat AS string), '')" +
            "       )) LIKE LOWER(CONCAT('%', CAST(:searchTerm AS string), '%'))" +
            "))")
    Page<Transaction> getFilteredTransactions(@Param("committee") Committee committee,
                                              @Param("txnType") TxnType txnType,
                                              @Param("year") Short year,
                                              @Param("building") Building building,
                                              @Param("donationType") DonationType donationType,
                                              @Param("txnMode") TxnMode txnMode,
                                              @Param("txnUserId") String txnUserId,
                                              @Param("userName") String userName,
                                              @Param("searchTerm") String searchTerm,
                                              Pageable pageable);


    @Query(value = "SELECT d.donor_building as building, coalesce(sum(t.amount),0) as amount " +
            " from transactions t " +
            "join donations d on t.id=d.id " +
            "where t.committee=:committee " +
            "and t.year=:year " +
            "and d.type <> 'OTHER' " +
            "group by d.donor_building " +
            "order by d.donor_building", nativeQuery = true)
    List<DonationStatsResponse> getDonationStatsByCommitteeAndYear(String committee, Short year);


    @Query(value = """
            with
            committee_totals AS (
                select committee, COALESCE(SUM(amount),0) AS total
                from transactions
                group by committee
            ),
            year_totals AS (
                select committee,year, COALESCE(SUM(amount),0) AS total
                from transactions
                group by committee,year
            ),
            year_txn_type_totals AS (
                select committee,year,txn_type, COALESCE(SUM(amount),0) AS total
                from transactions
                group by committee,year,txn_type
            ),
            year_donation_type_totals AS (
                select committee,year,type, COALESCE(SUM(amount),0) AS total
                from transactions
                join donations on transactions.id = donations.id
                group by committee,year,type
            ),
            year_agg AS (
                select
                       committee,
                       json_agg(json_build_object('year',year, 'total',total))::text AS data
                from year_totals
                group by committee
            ),
            year_txn_type_agg AS (
                select
                       committee,
                       json_agg(json_build_object('year',year, 'txnType',txn_type, 'total',total))::text AS data
                from year_txn_type_totals
                group by committee
            ),
            year_donation_type_agg AS (
                select
                       committee,
                       json_agg(json_build_object('year',year, 'donationType',type, 'total',total))::text AS data
                from year_donation_type_totals
                group by committee
            )
            SELECT
                   ct.committee, ct.total,
                   COALESCE(yg.data, '[]') AS balanceByYear,
                   COALESCE(ytg.data, '[]') AS balanceByYearAndTxnType,
                   COALESCE(ydg.data, '[]') AS balanceByYearAndDonationType
            from committee_totals ct
            LEFT JOIN year_agg yg ON ct.committee=yg.committee
            LEFT JOIN year_txn_type_agg ytg ON  ct.committee=ytg.committee
            LEFT JOIN year_donation_type_agg ydg ON ct.committee=ydg.committee
            WHERE ct.committee=:committee
            """, nativeQuery = true)
    List<CommitteeStats> getBalancesByCommittee(String committee);

    @Query("SELECT t FROM Transaction t " +
            "join fetch t.txnUser " +
            "left join fetch t.linked " +
            "WHERE t.id = :id")
    Optional<Transaction> findById(String id);

}

