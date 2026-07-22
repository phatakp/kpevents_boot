package com.phatakp.kpevents.transactions.repos;

import com.phatakp.kpevents.transactions.entity.ItemBooking;
import com.phatakp.kpevents.transactions.entity.ItemBookingId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemBookingRepository extends JpaRepository<ItemBooking, ItemBookingId> {
}