package ru.bank.branchatmservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Entity
@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "is_popular", nullable = false)
    private boolean isPopular;
}
