package ru.bank.branchatmservice.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.bank.branchatmservice.enums.EntityType;
import ru.bank.branchatmservice.model.WorkSchedule;

import java.util.List;
import java.util.UUID;

public class WorkScheduleSpecifications {
    public static Specification<WorkSchedule> entityIdsAre(List<UUID> entityIds, EntityType entityType) {
        return (root, query, cb) -> {
            if (entityIds == null || entityIds.isEmpty() || entityType == null) return null;
            return cb.and(
                    cb.equal(root.get("entityType"), entityType),
                    root.get("entityId").in(entityIds));
        };
    }
}
