package com.mailmak.time_registration_system.classes;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "comments"
)
public class Comment {
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
            name = "created_date",
            nullable = false,
            columnDefinition = "timestamp"
    )
    private LocalDateTime createdDate;

    @Column(
            name = "content",
            nullable = false,
            columnDefinition = "text"
    )
    private String content;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private Task task;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
