package ru.bank.branchatmservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Краткая информация об адресе")
public class AddressShortDto {
    @Schema(description = "Название города", example = "Москва")
    private String cityName;

    @Schema(description = "Тип улицы (например, улица, проспект)", example = "ул.")
    private String streetType;

    @Schema(description = "Название улицы", example = "Тверская")
    private String street;

    @Schema(description = "Номер дома", example = "15")
    private String house;
}
