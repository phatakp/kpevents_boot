package com.phatakp.kpevents.transactions.mapper;

import com.phatakp.kpevents.common.enums.DonationType;
import com.phatakp.kpevents.transactions.dto.request.TransactionRequest;
import com.phatakp.kpevents.transactions.dto.response.DonationResponse;
import com.phatakp.kpevents.transactions.dto.response.ItemBookingResponse;
import com.phatakp.kpevents.transactions.entity.Donation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DonationMapper {
    public static Donation toEntity(TransactionRequest request) {
        return Donation.builder()
                .type(request.donationType())
                .donorName(request.donorName())
                .donorBuilding(request.donorBuilding())
                .donorFlat(request.donorFlat())
                .build();
    }

    public static DonationResponse toResponse(Donation donation) {
        if (donation == null)
            return null;

        List<ItemBookingResponse> bookings = new ArrayList<>();
        if ((donation.getType().equals(DonationType.ANNADAAN)|| donation.getType().equals(DonationType.TEMPLE_ITEM)) && donation.getBookings() != null)
            bookings = donation.getBookings()
                    .stream().map(BookingMapper::toResponse).toList();

        return DonationResponse.builder()
                .type(donation.getType())
                .donorName(donation.getDonorName())
                .building(donation.getDonorBuilding())
                .flat(donation.getDonorFlat())
                .quantity(donation.getQuantity())
                .bookings(bookings)
                .build();
    }
}
