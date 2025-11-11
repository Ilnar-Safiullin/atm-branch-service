package ru.bank.branchatmservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record BranchShortInfo(
        @Schema(description = "Номер отделения", example = "101")
        String bankNumber
) {
}
