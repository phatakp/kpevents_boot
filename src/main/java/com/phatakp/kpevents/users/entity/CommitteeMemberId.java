package com.phatakp.kpevents.users.entity;

import com.phatakp.kpevents.common.enums.Committee;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class CommitteeMemberId implements Serializable {
    private static final long serialVersionUID = -1057499285504758093L;
    @Size(max = 20)
    @NotNull
    @Column(name = "committee", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Committee committee;

    @Size(max = 64)
    @NotNull
    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;


}