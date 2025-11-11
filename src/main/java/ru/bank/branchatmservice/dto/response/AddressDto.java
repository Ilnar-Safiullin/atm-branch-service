package ru.bank.branchatmservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {

    @Schema(description = "Название города", example = "Москва")
    private String cityName;

    @Schema(description = "Тип улицы (например, улица, проспект)", example = "ул.")
    private String streetType;

    @Schema(description = "Название улицы", example = "Тверская")
    private String street;

    @Schema(description = "Номер дома", example = "15")
    private String house;

    @Schema(description = "Название ближайшей станции метро", example = "Белорусская")
    private String metroStation;

    private GeoCoordinatesDto geoCoordinates;
}
