package com.mailmak.time_registration_system.classes;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;
@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "opt_outs"
)
public class OptOut {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(
            name = "id",
            updatable = false,
            nullable = false,
            columnDefinition = "uuid"
    )
    private UUID id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Embedded
    private Period period;
}
