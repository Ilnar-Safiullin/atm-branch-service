package ru.bank.branchatmservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SchedualOptional {
    ADD,
    CHANGE,
    DELETE;

    @JsonCreator
    public static SchedualOptional fromString(String value) {
        return value == null ? null : SchedualOptional.valueOf(value.toUpperCase());
    }
}
