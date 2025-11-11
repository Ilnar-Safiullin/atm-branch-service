package ru.bank.branchatmservice.specification;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.JoinType;
import ru.bank.branchatmservice.dto.request.AtmFilterDto;
import ru.bank.branchatmservice.enums.EntityType;
import ru.bank.branchatmservice.model.ATM;
import ru.bank.branchatmservice.model.WorkSchedule;

import java.time.LocalTime;
import java.util.UUID;

public class ATMSpecifications {

    public static Specification<ATM> byAtmFilterDto(AtmFilterDto filters) {
        return Specification.allOf(
                ATMSpecifications.inventoryNumberContains(filters.inventoryNumber()),
                ATMSpecifications.hasCashDeposit(filters.cashDeposit()),
                ATMSpecifications.hasNfc(filters.nfc()),
                ATMSpecifications.isWorkingNow(filters.workingNow()),
                ATMSpecifications.is24hour(filters.hour24()),

                ATMSpecifications.cityContains(filters.city()),
                ATMSpecifications.streetTypeContains(filters.streetType()),
                ATMSpecifications.streetContains(filters.street()),
                ATMSpecifications.houseContains(filters.house())
        );
    }

    public static Specification<ATM> inventoryNumberContains(String inventoryNumber) {
        return (root, query, cb) -> {
            if (inventoryNumber == null || inventoryNumber.isEmpty()) return null;
            return cb.like(root.get("inventoryNumber"), "%" + inventoryNumber + "%");
        };
    }

    public static Specification<ATM> cityContains(String city) {
        return (root, query, cb) -> {
            if (city == null || city.isEmpty()) return null;
            var joinCity = root.join("address", JoinType.LEFT).join("city", JoinType.LEFT);
            return cb.like(cb.lower(joinCity.get("name")), "%" + city.toLowerCase() + "%");
        };
    }

    public static Specification<ATM> streetTypeContains(String streetType) {
        return (root, query, cb) -> {
            if (streetType == null || streetType.isEmpty()) return null;
            var joinAddress = root.join("address", JoinType.LEFT);
            return cb.like(cb.lower(joinAddress.get("streetType")), "%" + streetType.toLowerCase() + "%");
        };
    }

    public static Specification<ATM> streetContains(String street) {
        return (root, query, cb) -> {
            if (street == null || street.isEmpty()) return null;
            var joinAddress = root.join("address", JoinType.LEFT);
            return cb.like(cb.lower(joinAddress.get("street")), "%" + street.toLowerCase() + "%");
        };
    }

    public static Specification<ATM> houseContains(String house) {
        return (root, query, cb) -> {
            if (house == null || house.isEmpty()) return null;
            var joinAddress = root.join("address", JoinType.LEFT);
            return cb.like(cb.lower(joinAddress.get("house")), "%" + house.toLowerCase() + "%");
        };
    }

    public static Specification<ATM> is24hour(Boolean is24hour) {
        return (root, query, cb) -> {
            if (is24hour == null) return null;
            var subquery = query.subquery(UUID.class);
            var wsRoot = subquery.from(WorkSchedule.class);
            if (is24hour) {
                subquery.select(wsRoot.get("entityId"))
                        .where(cb.and(
                                cb.equal(wsRoot.get("entityType"), EntityType.ATM),
                                cb.equal(wsRoot.get("openingTime"), LocalTime.of(0, 0, 0)),
                                cb.equal(wsRoot.get("closingTime"), LocalTime.of(0, 0, 0))
                        ));
            } else {
                subquery.select(wsRoot.get("entityId"))
                        .where(cb.and(
                                cb.equal(wsRoot.get("entityType"), EntityType.ATM),
                                cb.or(cb.notEqual(wsRoot.get("openingTime"), LocalTime.of(0, 0, 0)),
                                        cb.notEqual(wsRoot.get("closingTime"), LocalTime.of(0, 0, 0)))
                        ));
            }

            return root.get("id").in(subquery);
        };
    }

    public static Specification<ATM> isWorkingNow(Boolean workingNow) {

        return (root, query, cb) -> {
            if (workingNow == null ) return null;
            return cb.equal(root.get("isClosed"), !workingNow);
        };
    }

    public static Specification<ATM> hasCashDeposit(Boolean hasCashDeposit) {
        return (root, query, cb) -> {
            if (hasCashDeposit == null) return null;
            return cb.equal(root.get("hasCashDeposit"), hasCashDeposit);
        };
    }

    public static Specification<ATM> hasNfc(Boolean hasNfc) {
        return (root, query, cb) -> {
            if (hasNfc == null) return null;
            return cb.equal(root.get("hasNfc"), hasNfc);
        };
    }
}
