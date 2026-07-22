package com.phatakp.kpevents.transactions.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.phatakp.kpevents.common.enums.Committee;
import com.phatakp.kpevents.common.enums.TxnMode;
import com.phatakp.kpevents.common.enums.TxnType;
import com.phatakp.kpevents.common.exceptions.BusinessRuleException;
import com.phatakp.kpevents.users.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ToString
@Getter
@Setter
@Entity
@Builder
@Table(name = "transactions")
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Float amount;

    @NotNull
    @ColumnDefault("CURRENT_DATE")
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @NotNull
    @Column(name = "committee", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Committee committee;

    @NotNull
    @Column(name = "year", nullable = false)
    private Short year;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "txn_user_id", nullable = false)
    @ToString.Exclude
    private User txnUser;

    @NotNull
    @ColumnDefault("'DONATION'")
    @Column(name = "txn_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TxnType txnType;

    @NotNull
    @ColumnDefault("'ONLINE'")
    @Column(name = "txn_mode", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TxnMode txnMode;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "log_user_id", nullable = false)
    @ToString.Exclude
    private User logUser;

    @NotNull
    @Column(name = "created_at", nullable = false,updatable = false)
    @CreatedDate
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @NotNull
    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linked_id")
    @ToString.Exclude
    private Transaction linked;

    @OneToOne(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    @ToString.Exclude
    private Donation donation;

    // CUSTOM HELPER METHOD: Call this instead of relying solely on the builder field
    public void setDonation(Donation donation) {
        this.donation = donation;
        if (donation != null) {
            donation.setTransaction(this); // Crucial: assigns the back-reference!
        }
    }
}