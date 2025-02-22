package com.mailmak.time_registration_system.classes;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@SuperBuilder
@Setter
@Getter
@DiscriminatorValue("0")
@NoArgsConstructor
public class RecurringTask extends Task { }
