package ru.bank.branchatmservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "DTO информации о банкомате")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InfoAtmDto {
    @Schema(description = "Инвентарный номер банкомата", example = "ATM-12345")
    private String inventoryNumber;

    @Schema(description = "Название города", example = "Москва")
    private String cityName;

    @Schema(description = "Тип улицы", example = "ул.")
    private String streetType;

    @Schema(description = "Название улицы", example = "Ленина")
    private String street;

    @Schema(description = "Номер дома", example = "15А")
    private String house;
}