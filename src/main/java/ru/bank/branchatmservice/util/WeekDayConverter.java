package ru.bank.branchatmservice.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.bank.branchatmservice.enums.WeekDay;

@Converter(autoApply = true)
public class WeekDayConverter implements AttributeConverter<WeekDay, Integer> {

    @Override
    public Integer convertToDatabaseColumn(WeekDay weekDay) {
        if (weekDay == null) {
            return null;
        }
        return weekDay.getDayNumber();
    }

    @Override
    public WeekDay convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return WeekDay.fromNumber(dbData);
    }
}
