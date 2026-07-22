package com.phatakp.kpevents.transactions.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "item_bookings")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemBooking {
    @EmbeddedId
    private ItemBookingId bookingId;

    @MapsId("donationId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "donation_id", nullable = false)
    @ToString.Exclude
    private Donation donation;

    @MapsId("itemId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    @ToString.Exclude
    private Item item;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "quantity", nullable = false)
    private Float quantity;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "amount", nullable = false)
    private Float amount;

    @NotNull
    @Column(name = "year", nullable = false)
    private Short year;

    public ItemBooking(ItemBookingId itemBookingId,
                       @NotNull(message = "Booking quantity is required") Float qty,
                       @NotNull(message = "Booking amount is required") Float amt) {
        this.bookingId = itemBookingId;
        this.quantity = qty;
        this.amount = amt;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        ItemBooking that = (ItemBooking) o;
        return getBookingId() != null && Objects.equals(getBookingId(), that.getBookingId()) &&
                Objects.equals(getQuantity(), that.getQuantity()) &&
                Objects.equals(getAmount(), that.getAmount());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(bookingId, quantity, amount);
    }
}