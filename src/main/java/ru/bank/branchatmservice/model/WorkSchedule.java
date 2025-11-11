package ru.bank.branchatmservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.bank.branchatmservice.enums.EntityType;
import ru.bank.branchatmservice.enums.WeekDay;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Setter
@Getter
@ToString
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class WorkSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false)
    private EntityType entityType;

    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @Column(name = "week_day", nullable = false)
    private WeekDay weekDay;

    @Column(name = "opening_time", nullable = false)
    private LocalTime openingTime;

    @Column(name = "closing_time", nullable = false)
    private LocalTime closingTime;
}
