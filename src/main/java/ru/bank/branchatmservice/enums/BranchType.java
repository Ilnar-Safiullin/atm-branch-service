package ru.bank.branchatmservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum BranchType {
    HEAD_OFFICE("Головной офис", 1),
    BRANCH("Филиал", 2),
    ADDITIONAL_OFFICE("Дополнительный офис", 3),
    OPERATIONS_OFFICE("Операционный офис", 3),
    CREDIT_AND_CASH_OFFICE("Кредитно-кассовый офис", 4),
    BANKING_SERVICE_OFFICE("Офис банковского обслуживания", 4),
    INTERNAL_BANKING_UNIT("Внутреннее подразделение", 5),
    SUPPORTIVE_BANKING_UNIT("Вспомогательное подразделение", 5);

    private final String description;
    private final int level;


    @JsonCreator
    public static BranchType fromString(String value) {
        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(value) ||
                        type.getDescription().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный тип: " + value));
    }
}
