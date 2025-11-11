package ru.bank.branchatmservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bank.branchatmservice.enums.WeekDay;
import ru.bank.branchatmservice.model.WorkSchedule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkScheduleServiceTest {

    @InjectMocks
    private WorkScheduleService workScheduleService;

    @Test
    void isBranchOpenNow_WhenWorkSchedulesNull_ShouldReturnFalse() {
        boolean result = workScheduleService.isBranchOpenNow(null);
        assertFalse(result);
    }

    @Test
    void isBranchOpenNow_WhenWorkSchedulesEmpty_ShouldReturnFalse() {
        boolean result = workScheduleService.isBranchOpenNow(List.of());
        assertFalse(result);
    }

    @Test
    void isBranchOpenNow_WhenOpenNow_ShouldReturnTrue() {
        WorkSchedule schedule = mock(WorkSchedule.class);
        when(schedule.getWeekDay()).thenReturn(getCurrentWeekDay());
        when(schedule.getOpeningTime()).thenReturn(LocalTime.of(0, 0));
        when(schedule.getClosingTime()).thenReturn(LocalTime.of(23, 59, 59));
        boolean result = workScheduleService.isBranchOpenNow(List.of(schedule));
        assertTrue(result);
    }

    @Test
    void isBranchOpenNow_WhenClosedNow_ShouldReturnFalse() {
        WorkSchedule schedule = mock(WorkSchedule.class);
        when(schedule.getWeekDay()).thenReturn(getCurrentWeekDay());
        when(schedule.getOpeningTime()).thenReturn(LocalTime.of(0, 0));
        when(schedule.getClosingTime()).thenReturn(LocalTime.of(0, 0, 3));
        boolean result = workScheduleService.isBranchOpenNow(List.of(schedule));
        assertFalse(result);
    }

    private WeekDay getCurrentWeekDay() {
        DayOfWeek currentDayOfWeek = LocalDate.now().getDayOfWeek();
        return WeekDay.fromNumber(currentDayOfWeek.getValue());
    }
}