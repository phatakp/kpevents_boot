package com.phatakp.kpevents.transactions.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.phatakp.kpevents.common.enums.Building;
import com.phatakp.kpevents.common.enums.DonationType;
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
@Table(name = "donations")
@NoArgsConstructor
@AllArgsConstructor
public class Donation {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id", nullable = false)
    private Transaction transaction;

    @NotNull
    @Column(name = "type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private DonationType type;

    @Column(name = "donor_name", length = Integer.MAX_VALUE)
    private String donorName;

    @Column(name = "donor_building", length = 1)
    @Enumerated(EnumType.STRING)
    private Building donorBuilding;

    @Column(name = "donor_flat")
    private Short donorFlat;

    @ColumnDefault("0")
    @Column(name = "quantity")
    private Short quantity;

    @OneToMany(mappedBy = "donation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    @Builder.Default
    private List<ItemBooking> bookings = new ArrayList<>();
}