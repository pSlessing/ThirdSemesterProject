package com.mailmak.time_registration_system.classes;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@SuperBuilder
@Setter
@Getter
@NoArgsConstructor
@DiscriminatorValue("1")
public class ProjectSession extends Session {
    @Column(
            name = "description",
            columnDefinition = "text"
    )
    private String description;

    @Column(
            name = "state",
            nullable = false,
            columnDefinition = "integer"
    )
    private SessionState state;

    @ManyToOne(fetch = FetchType.EAGER)
    private Task task;

    public void complete() {
        if (this.state != SessionState.ACTIVE) {
            return;
        }

        this.state = SessionState.COMPLETED;
        this.getPeriod().setEndDate(LocalDateTime.now());
    }
}
