package com.mailmak.time_registration_system.classes;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "customers",
        uniqueConstraints = {
                @UniqueConstraint(name = "customer_name_unique", columnNames = "name")
        }
)
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(
            name = "id",
            updatable = false,
            nullable = false,
            columnDefinition = "uuid"
    )
    private UUID id;

    @Column(
            name = "name",
            nullable = false,
            columnDefinition = "text"
    )
    private String name;

    @ToString.Exclude
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Project> projects;
}
