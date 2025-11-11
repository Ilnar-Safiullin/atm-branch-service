package ru.bank.branchatmservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bank.branchatmservice.model.City;

import java.util.Optional;
import java.util.UUID;

public interface CityRepository extends JpaRepository<City, UUID> {
    Optional<City> findByName(String name);
}
