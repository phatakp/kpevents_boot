package com.phatakp.kpevents.transactions.repos;

import com.phatakp.kpevents.common.enums.ItemType;
import com.phatakp.kpevents.transactions.dto.response.ItemResponse;
import com.phatakp.kpevents.transactions.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i FROM Item i " +
            "left join fetch i.bookings b " +
            "left join fetch b.donation d " +
            "left join fetch d.transaction t " +
            "WHERE i.type = :itemType " +
            "order by i.itemName")
    List<Item> getItems(ItemType itemType);
}