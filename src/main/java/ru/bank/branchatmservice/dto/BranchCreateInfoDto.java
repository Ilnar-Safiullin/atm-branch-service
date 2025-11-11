package ru.bank.branchatmservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import ru.bank.branchatmservice.enums.BranchType;

@Schema(description = "Список информации об отделении банка")
public record BranchCreateInfoDto(
        @NotBlank(message = "Name is required.")
        @Pattern(regexp = "^[^a-zA-Z]*$", message = "Latin letters are not allowed")
        @Size(max = 64, message = "Name must not exceed 64 characters.")
        @Schema(description = "Наименование отделения", example = "ДО «ГУМ»")
        String name,

        @NotBlank(message = "Bank number is required.")
        @Pattern(
                regexp = "^(0|[1-9]\\d{0,3})$",
                message = "The bank number must be from 0 to 9999"
        )
        @Schema(description = "Номер отделения", example = "101")
        String bankNumber,

        @NotNull(message = "Currency exchange is required.")
        @Schema(description = "Оказываются ли услуги по обмену валюты", example = "true")
        Boolean hasCurrencyExchange,

        @NotNull(message = "Currency exchange is required.")
        @Schema(description = "Оказываются ли услуги по обмену валюты", example = "true")
        Boolean hasPandus,

        @NotBlank(message = "Phone number is required.")
        @Pattern(
                regexp = "^\\+7\\d{10}$",
                message = "The phone number must be like +79999999999"
        )
        @Schema(description = "Мобильный номер телефона", example = "+79999999999")
        String phoneNumber,

        @NotNull(message = "Type is required.")
        @Schema(description = "Тип структурного подразделения", example = "BRANCH")
        BranchType type
) {
}
