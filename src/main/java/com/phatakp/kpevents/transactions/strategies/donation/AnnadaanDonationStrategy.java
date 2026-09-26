package com.phatakp.kpevents.transactions.strategies.donation;

import com.phatakp.kpevents.admin.entity.Config;
import com.phatakp.kpevents.admin.services.ConfigService;
import com.phatakp.kpevents.common.enums.DonationType;
import com.phatakp.kpevents.common.exceptions.BusinessRuleException;
import com.phatakp.kpevents.transactions.dto.request.TransactionRequest;
import com.phatakp.kpevents.transactions.entity.*;
import com.phatakp.kpevents.transactions.mapper.BookingMapper;
import com.phatakp.kpevents.transactions.mapper.DonationMapper;
import com.phatakp.kpevents.transactions.repos.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnnadaanDonationStrategy implements DonationTypeStrategy {
    private final ConfigService configService;
    private final ItemRepository itemRepository;

    @Override
    public Donation createDonation(TransactionRequest request) {
        Config config = configService.getConfig();
        assertActiveAnnadaan(config, request.year());

        AtomicReference<Float> totalBookingAmount = new AtomicReference<>(0.0f);

        Donation donation = DonationMapper.toEntity(request);
        donation.setBookings(request.bookings().stream().map(booking -> {
            Item item = assertItemExists(booking.itemId());
            assertItemAvailable(item, request.year(), booking.bookingQty());

            totalBookingAmount.updateAndGet(v -> v + item.getPrice() * booking.bookingQty());

            ItemBooking itemBooking = BookingMapper.toEntity(booking, request.year());
            itemBooking.setItem(item);
            itemBooking.setDonation(donation);
            assertItemAvailable(item, request.year(), 0F);

            return itemBooking;
        }).toList());

        assertCorrectTotalAmt(request.amount(), totalBookingAmount.get());
        return donation;

    }

    @Override
    public Donation updateDonation(Transaction txn, TransactionRequest request) {
        Config config = configService.getConfig();
        assertActiveAnnadaan(config, request.year());


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
        return donation;
    }


    @Override
    public DonationType getDonationType() {
        return DonationType.ANNADAAN;
    }

    private void assertActiveAnnadaan(Config config, Short year) {
        if (!config.getActiveYear().equals(year)) {
            throw new BusinessRuleException("INVALID_YEAR", "Invalid year: " + year);
        }

        if (!config.getIsAnnadaanActive()) {
            throw new BusinessRuleException("ANNADAAN_NOT_ACTIVE", "Annadaan Not active");
        }
    }

    private Item assertItemExists(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new BusinessRuleException("ITEM_NOT_FOUND", "Item not found: " + itemId));
    }

    private void assertItemAvailable(Item item, Short year, Float bookingQty) {
        if (item.getAvailableQty(year) < bookingQty)
            throw new BusinessRuleException("ITEM_NOT_AVAILABLE", "Item not available: " + item.getItemName());
    }

    private void assertCorrectTotalAmt(Float amount, Float totalBookingAmount){
        if (!amount.equals(totalBookingAmount))
            throw new BusinessRuleException("INVALID_AMOUNT", "Total Amount not equal to booking amount");
    }
}
