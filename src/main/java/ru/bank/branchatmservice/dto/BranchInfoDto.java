package ru.bank.branchatmservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Информация об отделении")
public record BranchInfoDto(
        @Schema(description = "Наименование отделения", example = "ДО «ГУМ»")
        String name,

        @Schema(description = "Номер отделения", example = "101")
        String bankNumber,

        @Schema(description = "Номер телефона отделения банка", example = "+74951112233")
        String phoneNumber,

        AddressShortDto address
) {
}
