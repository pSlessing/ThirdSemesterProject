package com.mailmak.time_registration_system.classes;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@Setter
@Getter
@NoArgsConstructor
@DiscriminatorValue("0")
public class CheckInSession extends Session { }
