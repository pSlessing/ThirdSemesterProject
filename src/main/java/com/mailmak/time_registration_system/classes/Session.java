package com.mailmak.time_registration_system.classes;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Data
@SuperBuilder
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.INTEGER)
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "sessions"
)
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(
            name = "id",
            updatable = false,
            nullable = false,
            columnDefinition = "uuid"
    )
    private UUID id;

    @Embedded
    private Period period;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
