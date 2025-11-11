package ru.bank.branchatmservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageDto {
    @Schema(description = "Сообщение об операции", example = "Отделение успешно отредактировано")
    private String successfulMessage;
}
