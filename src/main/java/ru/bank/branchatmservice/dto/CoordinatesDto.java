package ru.bank.branchatmservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Координаты ATM")
public record CoordinatesDto(
        @Schema(description = "Широта", example = "55.671661")
        BigDecimal latitude,

        @Schema(description = "Долгота", example = "37.640670")
        BigDecimal longitude
) {
}
