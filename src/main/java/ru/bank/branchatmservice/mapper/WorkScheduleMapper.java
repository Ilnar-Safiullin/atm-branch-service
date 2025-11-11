package ru.bank.branchatmservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.bank.branchatmservice.dto.ScheduleDto;
import ru.bank.branchatmservice.dto.request.WorkScheduleUpdateDto;
import ru.bank.branchatmservice.enums.WeekDay;
import ru.bank.branchatmservice.model.WorkSchedule;

import java.time.LocalTime;
import java.util.List;

@Mapper(componentModel = "spring", imports = {WeekDay.class, LocalTime.class})
public interface WorkScheduleMapper {
    @Mapping(target = "weekDay", expression = "java(schedule.getWeekDay().getDayNumber())")
    @Mapping(target = "openingTime", expression = "java(schedule.getOpeningTime().toString())")
    @Mapping(target = "closingTime", expression = "java(schedule.getClosingTime().toString())")
    ScheduleDto ofWorkSchedule(WorkSchedule schedule);

    @Mapping(target = "weekDay", expression = "java(WeekDay.fromNumber(scheduleDto.weekDay()))")
    @Mapping(target = "openingTime", expression = "java(LocalTime.parse(scheduleDto.openingTime()))")
    @Mapping(target = "closingTime", expression = "java(LocalTime.parse(scheduleDto.closingTime()))")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "entityType", ignore = true)
    @Mapping(target = "entityId", ignore = true)
    WorkSchedule ofWorkScheduleDto(ScheduleDto scheduleDto);

    List<ScheduleDto> ofWorkSchedules(List<WorkSchedule> schedules);


    List<WorkSchedule> toListWorkSchedules(List<ScheduleDto> schedules);

    void ofWorkScheduleDtoList(List<ScheduleDto> scheduleDtoList, @MappingTarget List<WorkSchedule> schedules);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "entityType", ignore = true)
    @Mapping(target = "entityId", ignore = true)
    @Mapping(target = "weekDay", qualifiedByName = "getWeekDay", source = "weekDay")
    @Mapping(target = "openingTime", qualifiedByName = "parseStringTime", source = "openingTime")
    @Mapping(target = "closingTime", qualifiedByName = "parseStringTime", source = "closingTime")
    WorkSchedule toWorkSchedule(WorkScheduleUpdateDto workScheduleUpdateDto);

    @Named("parseStringTime")
    default LocalTime parseTime(String time) {
        if (time == null) {
            throw new IllegalArgumentException("time must not be null");
        }
        return LocalTime.parse(time);
    }

    @Named("getWeekDay")
    default WeekDay parseWeekDay(int weekDay) {
        return WeekDay.fromNumber(weekDay);
    }
}
