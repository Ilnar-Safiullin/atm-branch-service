package ru.bank.branchatmservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bank.branchatmservice.model.Location;

import java.util.UUID;

public interface LocationRepository extends JpaRepository<Location, UUID> {
}
