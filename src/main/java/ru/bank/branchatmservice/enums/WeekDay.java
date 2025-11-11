package ru.bank.branchatmservice.enums;

import lombok.Getter;

@Getter
public enum WeekDay {
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    SATURDAY(6),
    SUNDAY(7);

    private final int dayNumber;

    WeekDay(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public static WeekDay fromNumber(int number) {
        return switch (number) {
            case 1 -> MONDAY;
            case 2 -> TUESDAY;
            case 3 -> WEDNESDAY;
            case 4 -> THURSDAY;
            case 5 -> FRIDAY;
            case 6 -> SATURDAY;
            case 7 -> SUNDAY;
            default -> throw new IllegalArgumentException("Номер дня должен быть от 1 до 7: " + number);
        };
    }
}
