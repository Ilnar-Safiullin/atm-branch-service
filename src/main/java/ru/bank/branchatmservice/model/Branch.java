package ru.bank.branchatmservice.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLRestriction;
import ru.bank.branchatmservice.enums.BranchType;

import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "bank_number", nullable = false, unique = true)
    private String bankNumber;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    @Column(name = "has_currency_exchange", nullable = false)
    private boolean hasCurrencyExchange;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "has_pandus", nullable = false)
    private boolean hasPandus;

    @Column(name = "is_closed", nullable = false)
    private boolean isClosed;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "type", nullable = false)
    private BranchType type;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "entity_id", updatable = false, insertable = false)
    @SQLRestriction("entity_type = 'BRANCH'")
    private List<WorkSchedule> workSchedule;
}