package com.phatakp.kpevents.transactions.repos;

import com.phatakp.kpevents.common.enums.*;
import com.phatakp.kpevents.transactions.dto.response.CommitteeBalanceResponse;
import com.phatakp.kpevents.transactions.dto.response.CommitteeStats;
import com.phatakp.kpevents.transactions.dto.response.DonationStatsResponse;
import com.phatakp.kpevents.transactions.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    @Query(value = "SELECT t from Transaction t " +
            "join fetch t.txnUser " +
            "left join fetch t.donation d " +
            "where t.committee=:committee " +
            "and t.txnType=TxnType.EXPENSE " +
            "and t.year=:year " +
            "order by t.date desc")
    List<Transaction> getExpensesByCommitteeAndYear(@Param("committee") Committee committee,
                                                    @Param("year") Short year);

    @Query(value = "SELECT t from Transaction t " +
            "join fetch t.txnUser " +
            "left join fetch t.donation d " +
            "where t.committee=:committee " +
            "and t.txnType=TxnType.TRANSFER " +
            "and t.year=:year " +
            "and t.amount > 0 " +
            "order by t.date desc")
    List<Transaction> getTransfersByCommitteeAndYear(@Param("committee") Committee committee,
                                                    @Param("year") Short year);


    @Query(value = "SELECT t from Transaction t " +
            "left join fetch t.txnUser " +
            "left join fetch t.donation d " +
            "left join fetch d.bookings b " +
            "left join fetch b.item i " +
            "where t.committee=Committee.CULTURAL " +
            "and t.txnType=TxnType.DONATION " +
            "and b.year=:year " +
            "and i.type=ItemType.ANNADAAN " +
            "order by t.updatedAt desc")
    List<Transaction> getAnnadaanBookingsByYear(@Param("year") Short year);

    @Query(value = "SELECT t from Transaction t " +
            "left join fetch t.txnUser " +
            "left join fetch t.donation d " +
            "left join fetch d.bookings b " +
            "left join fetch b.item i " +
            "where t.committee=Committee.TEMPLE " +
            "and t.txnType=TxnType.DONATION " +
            "and i.type=ItemType.TEMPLE " +
            "order by t.updatedAt desc")
    List<Transaction> getTempleBookings();

    @Query(value = "SELECT t from Transaction t " +
            "join fetch t.txnUser " +
            "left join fetch t.donation d " +
            "where t.committee=:committee " +
            "and t.txnType=TxnType.DONATION " +
            "and t.year=:year " +
            "and d.type=DonationType.OTHER " +
            "order by t.updatedAt desc")
    List<Transaction> getOtherDonationsByCommitteeAndYear(@Param("committee") Committee committee,
                                                                @Param("year") Short year);

    @Query(value = "SELECT t from Transaction t " +
            "join fetch t.txnUser " +
            "left join fetch t.donation d " +
            "where t.committee=:committee " +
            "and t.txnType=TxnType.DONATION " +
            "and t.year=:year " +
            "and d.donorBuilding=:building " +
            "and d.type in (DonationType.CULTURAL, DonationType.TEMPLE)" +
            "order by d.donorBuilding,d.donorFlat")
    List<Transaction> getDonationsByCommitteeAndBuildingAndYear(@Param("committee") Committee committee,
                                                                @Param("building") Building building,
                                                                @Param("year") Short year);




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

