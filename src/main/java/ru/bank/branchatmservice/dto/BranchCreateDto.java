package ru.bank.branchatmservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import ru.bank.branchatmservice.dto.request.AddressFullInfoDto;

import java.util.List;
import java.util.UUID;

public record BranchCreateDto(
        @Valid
        @NotNull(message = "Branch information is required.")
        BranchCreateInfoDto branchInfo,

        @NotEmpty(message = "Department ids information is required.")
        @Schema(description = "Список отделов в отделении", example = "[\"94d3f9a2-fb96-45c0-bf4d-0ef2a174db02\", \"94d3f9a2-fb96-45c0-bf4d-0ef2a174db04\"]")
        List<UUID> departmentIds,

        @Valid
        @NotNull(message = "Address information is required")
        AddressFullInfoDto addressInfo,

        @Valid
        @NotEmpty(message = "Schedule information is required")
        List<ScheduleDto> scheduleArray
) {
}
