package ru.bank.branchatmservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Construction {
    INTERNAL("Внутренний"), EXTERNAL("Внешний");
    private String construction;

    @JsonValue
    public String getConstruction() {
        return construction;
    }

    @JsonCreator
    public static Construction fromString(String value) {
        if (value == null) return null;

        for (Construction construction : values()) {
            if (construction.construction.equalsIgnoreCase(value) ||
                    construction.name().equalsIgnoreCase(value)) {
                return construction;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + value);
    }
}
