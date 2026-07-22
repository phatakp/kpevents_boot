package com.phatakp.kpevents.transactions.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class ItemBookingId implements Serializable {
    private static final long serialVersionUID = 4975121646228779178L;
    @NotNull
    @Column(name = "donation_id", nullable = false, length = Integer.MAX_VALUE)
    private String donationId;

    @NotNull
    @Column(name = "item_id", nullable = false)
    private Long itemId;


}