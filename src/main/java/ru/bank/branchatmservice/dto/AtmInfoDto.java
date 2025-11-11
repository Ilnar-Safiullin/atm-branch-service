package ru.bank.branchatmservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Список информации об ATM")
public record AtmInfoDto(
        @Schema(description = "флаг, работает/ архивирование", example = "Москва")
        Boolean status,

        AddressShortDto address,

        CoordinatesDto geoCoordinates,

        @Schema(description = "Название ближайшей станции метро", example = "Ленина")
        String metroStation,

        @Schema(description = "Порядковый номер банкомата", example = "11")
        String number,

        @Schema(description = "Описание места установки", example = "Второй этаж, левое крыло")
        String installationLocation,

        @Schema(description = "Тип конструкции банкомата", example = "Внешний")
        String construction,

        @Schema(description = "Внесение наличных", example = "true")
        Boolean cashDeposit,

        @Schema(description = "Бесконтактная оплата", example = "true")
        Boolean nfc
) {
}
