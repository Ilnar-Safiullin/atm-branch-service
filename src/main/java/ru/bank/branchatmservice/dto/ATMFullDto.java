package ru.bank.branchatmservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Информация о конкретном банкомате")
public record ATMFullDto(
        AtmInfoDto atmInfo,
        BranchInfoDto branchInfo,
        List<ScheduleDto> schedule
) {
}
