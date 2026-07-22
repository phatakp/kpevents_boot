package com.phatakp.kpevents.transactions.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.phatakp.kpevents.common.enums.ItemType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@Entity
@Table(name = "items")
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "item_name", nullable = false, length = Integer.MAX_VALUE)
    private String itemName;

    @NotNull
    @Column(name = "type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ItemType type;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "price", nullable = false)
    private Float price;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "quantity", nullable = false)
    private Float quantity;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "amount", nullable = false)
    private Float amount;


    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    @ToString.Exclude
    @Builder.Default
    private List<ItemBooking> bookings = new ArrayList<>();

    public float getAvailableQty(Short year){
        var booked = bookings.stream()
                .filter(b -> b.getYear().equals(year))
                .mapToDouble(ItemBooking::getQuantity)
                .sum();
        return (float) (quantity - booked);
    }
    public float getAvailableAmt(Short year){
        var booked = bookings.stream()
                .filter(b -> b.getYear().equals(year))
                .mapToDouble(ItemBooking::getAmount)
                .sum();
        return (float) (amount - booked);
    }


}