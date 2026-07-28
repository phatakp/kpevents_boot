package com.phatakp.kpevents.transactions.strategies.donation;

import com.phatakp.kpevents.admin.entity.Config;
import com.phatakp.kpevents.admin.services.ConfigService;
import com.phatakp.kpevents.common.enums.Building;
import com.phatakp.kpevents.common.enums.Committee;
import com.phatakp.kpevents.common.enums.DonationType;
import com.phatakp.kpevents.common.enums.ItemType;
import com.phatakp.kpevents.common.exceptions.BusinessRuleException;
import com.phatakp.kpevents.transactions.dto.request.TransactionRequest;
import com.phatakp.kpevents.transactions.dto.response.TransactionPageResponse;
import com.phatakp.kpevents.transactions.dto.response.TransactionResponse;
import com.phatakp.kpevents.transactions.entity.*;
import com.phatakp.kpevents.transactions.mapper.BookingMapper;
import com.phatakp.kpevents.transactions.mapper.DonationMapper;
import com.phatakp.kpevents.transactions.mapper.TransactionMapper;
import com.phatakp.kpevents.transactions.repos.ItemRepository;
import com.phatakp.kpevents.transactions.repos.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnnadaanDonationStrategy implements DonationTypeStrategy {
    private final ConfigService configService;
    private final ItemRepository itemRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public Donation createDonation(TransactionRequest request) {
        Config config = configService.getConfig();

        if (!config.getActiveYear().equals(request.year())) {
            throw new BusinessRuleException("INVALID_YEAR", "Invalid year: " + request.year());
        }

        if (!config.getIsAnnadaanActive()) {
            throw new BusinessRuleException("ANNADAAN_NOT_ACTIVE", "Annadaan Not active");
        }

        Donation donation = DonationMapper.toEntity(request);

        AtomicReference<Float> totalBookingAmount = new AtomicReference<>(0.0f);

        donation.setBookings(request.bookings().stream().map(booking -> {
            Item item = itemRepository.findById(booking.itemId()).orElse(null);
            if (item == null)
                throw new BusinessRuleException("INVALID_BOOKING_ITEM", "Invalid booking item: " + booking.itemId());

            if (item.getAvailableQty(request.year()) < booking.bookingQty())
                throw new BusinessRuleException("ITEM_NOT_AVAILABLE", "Item not available: " + item.getItemName());

            totalBookingAmount.updateAndGet(v -> v + item.getPrice() * booking.bookingQty());

            ItemBooking itemBooking = BookingMapper.toEntity(booking, request.year());
            itemBooking.setItem(item);
            itemBooking.setDonation(donation);

            if (item.getAvailableQty(request.year()) < 0)
                throw new BusinessRuleException("ITEM_NOT_AVAILABLE", "Item no longer available: " + item.getItemName());
            return itemBooking;
        }).toList());


        if (!request.amount().equals(totalBookingAmount.get()))
            throw new BusinessRuleException("INVALID_AMOUNT", "Total Amount not equal to booking amount");

        return donation;

    }

    @Override
    public Donation updateDonation(Transaction txn, TransactionRequest request) {
        Config config = configService.getConfig();

        if (!config.getActiveYear().equals(request.year())) {
            throw new BusinessRuleException("INVALID_YEAR", "Invalid year: " + request.year());
        }

        if (!config.getIsAnnadaanActive()) {
            throw new BusinessRuleException("ANNADAAN_NOT_ACTIVE", "Annadaan Not active");
        }


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
    public TransactionPageResponse getDonations(Committee committee, Short year, Building building, DonationType donationType) {
        List<TransactionResponse> txns = transactionRepository.getAnnadaanBookingsByYear(year)
                .stream()
                .map(TransactionMapper::toResponse).toList();
        return TransactionMapper.toPageResponse(txns);
    }

    @Override
    public DonationType getDonationType() {
        return DonationType.ANNADAAN;
    }


}
