package ru.bank.branchatmservice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Список информации об ATM")
public record AtmInfoCreateDto(

        @Schema(description = "Номер банкомата", example = "1111")
        String ATMNumber,

        @Schema(description = "Инвентарный номер", example = "0001244890")
        String inventoryNumber,

        @Schema(description = "Уточняющий комментарий к адресу", example = "Второй этаж, левое крыло")
        String installationLocation,

        @Schema(description = "Тип конструкции банкомата", example = "Внешний")
        String construction,

        @Schema(description = "Внесение наличных", example = "true")
        Boolean cashDeposit,

        @JsonProperty("NFC")
        @Schema(description = "Бесконтактная оплата", example = "true")
        Boolean nfc
) {
}
