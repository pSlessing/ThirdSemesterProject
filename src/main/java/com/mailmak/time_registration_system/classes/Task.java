package com.mailmak.time_registration_system.classes;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Data
@SuperBuilder
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.INTEGER)
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "tasks",
        uniqueConstraints = {
                @UniqueConstraint(name = "task_name_unique_for_project", columnNames = {"name", "project_id"})
        }
)
public class Task {
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

    @Column(
            name = "description",
            columnDefinition = "text"
    )
    private String description;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    private Project project;

    @ToString.Exclude
    @ManyToMany(mappedBy = "assignedTasks", fetch = FetchType.EAGER)
    private List<User> assignedUsers;

    @ToString.Exclude
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectSession> projectSessions;

    @ToString.Exclude
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    public void assignUser(User user) {
        if (this.assignedUsers == null) {
            this.assignedUsers = new ArrayList<>();
        }

        this.assignedUsers.add(user);
    }

    public void unassignUser(User user) {
        if (this.assignedUsers == null) {
            this.assignedUsers = new ArrayList<>();
        }

        this.assignedUsers.remove(user);
    }
}
