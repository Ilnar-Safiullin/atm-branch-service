package ru.bank.branchatmservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record MessageResponseDto(
        @Schema(description = "Сообщение об операции", example = "Успешно добавлен")
        String message,

        @Schema(description = "Дата", example = "2025-07-22T17:30:00Z")
        String createDate
) {
}
