package ru.bank.branchatmservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Id и имя филиала банка")
public record BranchShortDto(
        @Schema(description = "Идентификатор отделения", example = "42e0623c-6e6d-11ed-a1eb-0242ac120004")
        UUID bankBranchId,

        @Schema(description = "Наименование отделения", example = "ДО «ГУМ»")
        String bankBranchName
) {
}