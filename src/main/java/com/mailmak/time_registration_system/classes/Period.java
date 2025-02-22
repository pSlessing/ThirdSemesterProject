package com.mailmak.time_registration_system.classes;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Builder
@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Period {
    @Column(
            name = "start_date",
            nullable = false,
            columnDefinition = "timestamp"
    )
    private LocalDateTime startDate;

    @Column(
            name = "end_date",
            columnDefinition = "timestamp"
    )
    private LocalDateTime endDate;
}
