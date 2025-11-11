package ru.bank.branchatmservice.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "atm")
@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ATM {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "branch_id", referencedColumnName = "id")
    private Branch branch;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    @Column(name = "number")
    private String number;

    @Column(name = "inventory_number")
    private String inventoryNumber;

    @Column(name = "installation_location")
    private String installationLocation;

    @Column(name = "construction")
    private String construction;

    @Column(name = "has_cash_deposit")
    private boolean hasCashDeposit;

    @Column(name = "has_nfc")
    private boolean hasNfc;

    @Column(name = "is_closed")
    private boolean isClosed;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "entity_id", updatable = false, insertable = false)
    @SQLRestriction("entity_type = 'ATM'")
    private List<WorkSchedule> workSchedule;
}
