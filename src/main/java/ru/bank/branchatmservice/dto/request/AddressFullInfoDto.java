package ru.bank.branchatmservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressFullInfoDto(
        @NotBlank(message = "City name is required.")
        @Size(max = 30, message = "City name must not exceed 30 characters.")
        @Schema(description = "Город", example = "Москва")
        String cityName,

        @NotBlank(message = "Street type is required.")
        @Size(max = 6, message = "Street type must not exceed 6 characters.")
        @Schema(description = "Вид улицы", example = "ал.")
        String streetType,

        @NotBlank(message = "Street is required.")
        @Size(max = 64, message = "Street must not exceed 64 characters.")
        @Schema(description = "Название улицы", example = "Ленина")
        String street,

        @NotBlank(message = "House is required.")
        @Size(max = 10, message = "Name must not exceed 10 characters.")
        @Schema(description = "Номер дома", example = "143")
        String house,

        @Pattern(
                regexp = "^-?([1-8]?[0-9](\\.\\d{1,6})?|90(\\.0{1,6})?)$",
                message = "Latitude must be between -90 and 90 with up to 6 decimal places"
        )
        @Schema(description = "Широта", example = "34.456789")
        String latitude,

        @Pattern(
                regexp = "^-?((1[0-7][0-9]|[1-9]?[0-9])(\\.\\d{1,6})?|180(\\.0{1,6})?)$",
                message = "Longitude must be between -180 and 180 with up to 6 decimal places"
        )
        @Schema(description = "Долгота", example = "14.345678")
        String longitude,

        @Size(max = 25, message = "Metro station must not exceed 25 characters.")
        @Schema(description = "Название ближайшей станции метро", example = "Белорусская")
        String metroStation
) {
}
