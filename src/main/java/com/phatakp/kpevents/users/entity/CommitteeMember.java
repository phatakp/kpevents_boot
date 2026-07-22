package com.phatakp.kpevents.users.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Builder
@Getter
@Setter
@Entity
@Table(name = "committee_members")
@NoArgsConstructor
@AllArgsConstructor
public class CommitteeMember {
    @EmbeddedId
    private CommitteeMemberId memberId;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = Boolean.FALSE;


}