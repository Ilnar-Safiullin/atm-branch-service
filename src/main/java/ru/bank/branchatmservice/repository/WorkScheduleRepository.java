package ru.bank.branchatmservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.bank.branchatmservice.enums.EntityType;
import ru.bank.branchatmservice.model.WorkSchedule;

import java.util.List;
import java.util.UUID;

public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, UUID>, JpaSpecificationExecutor<WorkSchedule> {
    List<WorkSchedule> findAllByEntityIdAndEntityType(UUID entityId, EntityType entityType);

    void deleteAllByEntityIdAndEntityType(UUID atmId, EntityType entityType);
}
