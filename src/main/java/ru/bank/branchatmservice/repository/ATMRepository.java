package ru.bank.branchatmservice.repository;

import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.bank.branchatmservice.model.ATM;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ATMRepository extends JpaRepository<ATM, UUID>, JpaSpecificationExecutor<ATM> {

    @Modifying
    @Query(value = """
            UPDATE atm SET is_closed = true 
            WHERE id IN (:ids) 
            RETURNING *
            """,
            nativeQuery = true)
    List<ATM> softDeleteByAtmIds(@Param("ids") List<UUID> ids);

    Optional<ATM> findByIdAndIsClosedFalse(UUID id);

    boolean existsByNumber(String number);

    @Nonnull
    @EntityGraph(attributePaths = {"address", "address.city", "workSchedule"})
    List<ATM> findAll();

    @Nonnull
    @EntityGraph(attributePaths = {"address", "address.city", "workSchedule"})
    List<ATM> findAll(@Nonnull Specification<ATM> spec);
}
