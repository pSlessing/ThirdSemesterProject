package com.mailmak.time_registration_system.classes;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@SuperBuilder
@DiscriminatorValue("1")
@NoArgsConstructor
public class CompletableTask extends Task {
    @Column(
            name = "deadline",
            columnDefinition = "timestamp"
    )
    private LocalDateTime deadline;

    @Column(
            name = "state",
            columnDefinition = "integer"
    )
    private TaskState state;
}
