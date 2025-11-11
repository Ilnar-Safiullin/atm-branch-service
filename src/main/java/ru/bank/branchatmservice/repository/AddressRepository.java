package ru.bank.branchatmservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bank.branchatmservice.model.Address;

import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
}
