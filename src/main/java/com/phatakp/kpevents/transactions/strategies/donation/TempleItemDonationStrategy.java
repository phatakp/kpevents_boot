package com.phatakp.kpevents.transactions.strategies.donation;

import com.phatakp.kpevents.common.enums.DonationType;
import com.phatakp.kpevents.common.exceptions.BusinessRuleException;
import com.phatakp.kpevents.transactions.dto.request.TransactionRequest;
import com.phatakp.kpevents.transactions.entity.*;
import com.phatakp.kpevents.transactions.mapper.BookingMapper;
import com.phatakp.kpevents.transactions.mapper.DonationMapper;
import com.phatakp.kpevents.transactions.repos.DonationRepository;
import com.phatakp.kpevents.transactions.repos.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TempleItemDonationStrategy implements DonationTypeStrategy {
    private final ItemRepository itemRepository;
    private final DonationRepository donationRepository;

    @Override
    public Donation createDonation(TransactionRequest request) {

        Donation donation = DonationMapper.toEntity(request);

        AtomicReference<Float> totalBookingAmount = new AtomicReference<>(0.0f);

        donation.setBookings(request.bookings().stream().map(booking -> {
            Item item = itemRepository.findById(booking.itemId()).orElse(null);
            if (item == null)
                throw new BusinessRuleException("INVALID_BOOKING_ITEM", "Invalid booking item: " + booking.itemId());

            if (item.getAvailableAmt(request.year()) < booking.bookingAmt())
                throw new BusinessRuleException("ITEM_NOT_AVAILABLE", "Item not available: " + item.getItemName());


            ItemBooking itemBooking = BookingMapper.toEntity(booking, request.year());
            itemBooking.setItem(item);
            itemBooking.setDonation(donation);
            totalBookingAmount.updateAndGet(v -> v + booking.bookingAmt());

            if (item.getAvailableAmt(request.year()) < 0)
                throw new BusinessRuleException("ITEM_NOT_AVAILABLE", "Item no longer available: " + item.getItemName());
            return itemBooking;
        }).toList());

        if (!request.amount().equals(totalBookingAmount.get()))
            throw new BusinessRuleException("INVALID_AMOUNT", "Total Amount not equal to booking amount");

        return donation;

    }

    @Override
    public Donation updateDonation(Transaction txn, TransactionRequest request) {
        Donation donation = txn.getDonation();
        donation.setDonorBuilding(request.donorBuilding());
        donation.setDonorFlat(request.donorFlat());
        donation.setDonorName(request.donorName());

        Set<ItemBooking> existing = new HashSet<>(donation.getBookings());
        Set<ItemBooking> requested = request.bookings().stream()
                .map(b -> new ItemBooking(
                        new ItemBookingId(donation.getId(), b.itemId()),
                        b.bookingQty(), b.bookingAmt()))
                .collect(Collectors.toSet());
        if (!existing.equals(requested))
            throw new BusinessRuleException("INVALID_ITEMS", "Booking Items cannot be changed");

        donation.setTransaction(txn);
        return donationRepository.save(donation);

    }


    @Override
    public DonationType getDonationType() {
        return DonationType.TEMPLE_ITEM;
    }


}
