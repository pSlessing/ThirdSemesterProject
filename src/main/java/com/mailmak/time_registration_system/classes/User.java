package com.mailmak.time_registration_system.classes;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "user_principal_id_unique", columnNames = "principal_id"),
                @UniqueConstraint(name = "user_email_unique", columnNames = "email")
        }
)
public class User {
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
            name = "principal_id",
            updatable = false,
            nullable = false,
            columnDefinition = "uuid"
    )
    private UUID principalID;

    @Column(
            name = "name",
            nullable = false,
            columnDefinition = "text"
    )
    private String name;

    @Column(
            name = "email",
            updatable = false,
            nullable = false,
            columnDefinition = "text"
    )
    private String email;

    @Column(
            name = "roles",
            nullable = false,
            columnDefinition = "integer"
    )
    private Role roles;

    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Session> sessions;

    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OptOut> optOuts;

    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @ToString.Exclude
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_tasks",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id")
    )
    private List<Task> assignedTasks;
}