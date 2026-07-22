package com.phatakp.kpevents.admin.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "config")
public class Config {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Short activeYear;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isAnnadaanActive = false;
}
