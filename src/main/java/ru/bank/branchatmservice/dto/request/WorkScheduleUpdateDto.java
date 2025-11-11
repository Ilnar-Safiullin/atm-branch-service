package ru.bank.branchatmservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import ru.bank.branchatmservice.enums.SchedualOptional;

@Data
public class WorkScheduleUpdateDto {
    @NotNull(message = "День недели обязателен")
    @Min(value = 1, message = "День недели должен быть от 1 до 7")
    @Max(value = 7, message = "День недели должен быть от 1 до 7")
    @Schema(description = "День недели в графике работы", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private int weekDay;

    @Pattern(regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Время открытия должно быть в формате HH:MM")
    @Schema(description = "Время открытия", example = "8:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String openingTime;

    @Pattern(regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Время закрытия должно быть в формате HH:MM")
    @Schema(description = "Время закрытия", example = "21:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String closingTime;

    @NotNull(message = "Операция обязательна")
    @Schema(description = "Действие которое нужно совершить с графиком работы", example = "ADD|DELETE|CHANGE",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private SchedualOptional optional;
}