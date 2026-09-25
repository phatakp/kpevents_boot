package com.phatakp.kpevents.transactions.repos;

import com.phatakp.kpevents.transactions.dto.response.ItemProjection;
import com.phatakp.kpevents.transactions.entity.Item;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = """
                WITH balance as (
                        SELECT i.id,
                               i.type,
                               COALESCE(SUM(b.amount), 0) bookedAmt,
                               COALESCE(SUM(b.quantity), 0) bookedQty
                        FROM item_bookings b
                        JOIN items i on i.id=b.item_id
                        JOIN donations d on d.id=b.donation_id
                        JOIN transactions t on t.id=d.id
                        WHERE t.year = :year
                        group by i.id, i.type
                        )
                , final as (SELECT i.*,
                                   i.quantity-coalesce(b.bookedQty,0) availableQty,
                                   i.amount-coalesce(b.bookedAmt,0) availableAmt
                FROM items i
                LEFT JOIN balance b on i.id=b.id and i.type=b.type
                WHERE i.type=:itemType)
                SELECT final.id,
                       final.item_name itemName,
                       final.type,
                       final.price,
                       final.quantity,
                       final.amount,
                       final.availableQty,
                       final.availableAmt
                from final
                where final.availableAmt>0 OR final.availableQty>0
            """,
            countQuery = """
                        WITH balance as (
                                SELECT i.id,
                                       i.type,
                                       COALESCE(SUM(b.amount), 0) bookedAmt,
                                       COALESCE(SUM(b.quantity), 0) bookedQty
                                FROM item_bookings b
                                JOIN items i on i.id=b.item_id
                                JOIN donations d on d.id=b.donation_id
                                JOIN transactions t on t.id=d.id
                                WHERE t.year = :year
                                group by i.id, i.type
                                )
                        , final as (SELECT i.*,
                                           i.quantity-coalesce(b.bookedQty,0) availableQty,
                                           i.amount-coalesce(b.bookedAmt,0) availableAmt
                        FROM items i
                        LEFT JOIN balance b on i.id=b.id and i.type=b.type
                        WHERE i.type=:itemType)
                        SELECT count(*)
                        from final
                        where final.availableAmt>0 OR final.availableQty>0
                    """,
            nativeQuery = true)
    Page<ItemProjection> getAvailableItemsForYear(String itemType, Short year, Pageable pageable);

    @Query(value = """
                SELECT i.*,
                       i.quantity availableQty,
                       i.amount availableAmt
                FROM items i
                where i.type='ANNADAAN'
            """,
            countQuery = """
                        SELECT count(*)
                        FROM items i
                        where i.type='ANNADAAN'
                    """,
            nativeQuery = true)
    Page<ItemProjection> getAnnadaanItems(Pageable pageable);

    boolean existsByItemName(String itemName);

    Optional<Item> findItemByItemName(String itemName);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Item i " +
            "LEFT JOIN FETCH i.bookings " +
            "WHERE i.id = :id")
    Optional<Item> findById(Long id);
}