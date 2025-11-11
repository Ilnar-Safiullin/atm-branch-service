package ru.bank.branchatmservice.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;

public record AtmFilterDto(
        @Parameter(
                name = "inventoryNumber",
                in = ParameterIn.QUERY,
                description = "Инвентарный номер",
                example = "0001244890",
                schema = @Schema(type = "string")
        )
        String inventoryNumber,

        @Parameter(
                name = "hour24",
                in = ParameterIn.QUERY,
                description = "Признак, работает ли банкомат круглосуточно.",
                example = "true",
                schema = @Schema(type = "boolean")
        )
        Boolean hour24,

        @Parameter(
                name = "workingNow",
                in = ParameterIn.QUERY,
                description = "Признак, работает ли банкомат в текущий день и время.",
                example = "true",
                schema = @Schema(type = "boolean")
        )
        Boolean workingNow,

        @Parameter(
                name = "cashDeposit",
                in = ParameterIn.QUERY,
                description = "Внесение наличных",
                example = "true",
                schema = @Schema(type = "boolean")
        )
        Boolean cashDeposit,

        @Parameter(
                name = "nfc",
                in = ParameterIn.QUERY,
                description = "Бесконтактная оплата",
                example = "true",
                schema = @Schema(type = "boolean")
        )
        Boolean nfc,

        @Parameter(
                name = "city",
                in = ParameterIn.QUERY,
                description = "Город",
                example = "Москва",
                schema = @Schema(type = "string")
        )
        String city,

        @Parameter(
                name = "streetType",
                in = ParameterIn.QUERY,
                description = "Тип улицы",
                example = "ул",
                schema = @Schema(type = "string")
        )
        String streetType,

        @Parameter(
                name = "street",
                in = ParameterIn.QUERY,
                description = "Улица",
                example = "Ленина",
                schema = @Schema(type = "string")
        )
        String street,

        @Parameter(
                name = "house",
                in = ParameterIn.QUERY,
                description = "Дом",
                example = "173",
                schema = @Schema(type = "string")
        )
        String house
) {}
