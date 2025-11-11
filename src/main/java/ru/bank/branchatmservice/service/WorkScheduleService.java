package ru.bank.branchatmservice.service;

import org.springframework.stereotype.Service;
import ru.bank.branchatmservice.enums.WeekDay;
import ru.bank.branchatmservice.model.WorkSchedule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class WorkScheduleService {

    public boolean isBranchOpenNow(List<WorkSchedule> workSchedules) {
        if (workSchedules == null || workSchedules.isEmpty()) {
            return false;
        }

        DayOfWeek currentDayOfWeek = LocalDate.now().getDayOfWeek();
        WeekDay currentWeekDay = WeekDay.fromNumber(currentDayOfWeek.getValue());
        LocalTime currentTime = LocalTime.now();

        return workSchedules.stream()
                .filter(schedule -> schedule.getWeekDay() == currentWeekDay)
                .anyMatch(schedule -> isTimeInSchedule(currentTime, schedule));
    }

    private boolean isTimeInSchedule(LocalTime currentTime, WorkSchedule schedule) {
        return !currentTime.isBefore(schedule.getOpeningTime())
                && !currentTime.isAfter(schedule.getClosingTime());
    }
}