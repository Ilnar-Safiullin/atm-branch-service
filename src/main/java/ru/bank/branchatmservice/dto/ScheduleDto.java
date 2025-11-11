package ru.bank.branchatmservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Range;

@Schema(description = "Время работы")
public record ScheduleDto(
        @NotNull
        @Range(min = 1, max = 7, message = "Week day must be between 1 and 7")
        @Schema(description = "День недели 1 — пн, 7 — вс", example = "1")
        Integer weekDay,

        @NotBlank
        @Pattern(
                regexp = "^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$",
                message = "Time must be in HH:mm format (00:00 - 23:59)"
        )
        @Schema(description = "Время начала работы", example = "08:00")
        String openingTime,

        @NotBlank
        @Pattern(
                regexp = "^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$",
                message = "Time must be in HH:mm format (00:00 - 23:59)"
        )
        @Schema(description = "Время завершения работы", example = "19:00")
        String closingTime
) {
}
