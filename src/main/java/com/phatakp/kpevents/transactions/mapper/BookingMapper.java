package com.phatakp.kpevents.transactions.mapper;

import com.phatakp.kpevents.transactions.dto.request.BookingRequest;
import com.phatakp.kpevents.transactions.dto.response.ItemBookingResponse;
import com.phatakp.kpevents.transactions.entity.ItemBooking;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {
    public static ItemBooking toEntity(BookingRequest request,short year) {
        return ItemBooking.builder()
                .quantity(request.bookingQty())
                .amount(request.bookingAmt())
                .year(year)
                .build();
    }

    public static ItemBookingResponse toResponse(ItemBooking booking) {
        if (booking == null) {
            return null;
        }
        return ItemBookingResponse.builder()
                .itemId(booking.getItem().getId())
                .itemName(booking.getItem().getItemName())
                .itemType(booking.getItem().getType())
                .price(booking.getItem().getPrice())
                .bookingQty(booking.getQuantity())
                .bookingAmt(booking.getAmount())
                .totalQty(booking.getItem().getAvailableQty(booking.getYear()))
                .totalAmt(booking.getItem().getAvailableAmt(booking.getYear()))
                .build();
    }
}
