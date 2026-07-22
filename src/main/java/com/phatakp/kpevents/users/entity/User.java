package com.phatakp.kpevents.users.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.phatakp.kpevents.common.enums.Building;
import com.phatakp.kpevents.common.enums.UserRole;
import com.phatakp.kpevents.transactions.entity.Transaction;
import com.phatakp.kpevents.users.dto.response.UserMembership;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User{
    @Id
    @Column(name = "clerk_id", nullable = false, length = 64)
    private String clerkId;

    @NotNull
    @Column(name = "email", nullable = false, length = Integer.MAX_VALUE)
    private String email;

    @NotNull
    @Column(name = "first_name", nullable = false, length = Integer.MAX_VALUE)
    private String firstName;

    @Column(name = "last_name", length = Integer.MAX_VALUE)
    private String lastName;

    @Column(name = "image_url", length = Integer.MAX_VALUE)
    private String imageUrl;

    @NotNull
    @Column(name = "role", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserRole role = UserRole.USER;

    @NotNull
    @Column(name = "building", nullable = false, length = 1)
    @Enumerated(EnumType.STRING)
    private Building building;

    @NotNull
    @Column(name = "flat", nullable = false)
    private Short flat;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private List<CommitteeMember> memberships;

    @OneToMany(mappedBy = "txnUser",fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private List<Transaction> userTxns;


    public String getUserInfo(){
        return firstName + " " + lastName + "(" + building.name() + flat + ")";
    }

}