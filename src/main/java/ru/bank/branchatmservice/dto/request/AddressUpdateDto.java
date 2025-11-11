package ru.bank.branchatmservice.dto.request;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class AddressUpdateDto {
    @Schema(description = "UUID", example = "b6ae1b8e-a8c9-11f0-b066-9bff96e3237e",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UUID cityId;

    @Size(max = 6, message = "Тип улицы не должен превышать 6 символов")
    @Schema(description = "Вид улицы", example = "ал.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String streetType;

    @Size(max = 64, message = "Название улицы не должно превышать 64 символов")
    @Schema(description = "Название улицы", example = "Ленина", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String street;

    @Size(max = 10, message = "Номер дома не должен превышать 10 символов")
    @Schema(description = "Номер дома", example = "143", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String house;

    @Size(max = 25, message = "Название станции метро не должно превышать 25 символов")
    @Schema(description = "Название ближайшей станции метро", example = "Белорусская",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String metroStation;

    @Pattern(
            regexp = "^-?([1-8]?[0-9](\\.\\d{1,6})?|90(\\.0{1,6})?)$",
            message = "Широта должна быть между -90 и 90 и до 6 знаков после запятой"
    )
    @Schema(description = "Широта", example = "34.456789", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String latitude;

    @Pattern(
            regexp = "^-?((1[0-7][0-9]|[1-9]?[0-9])(\\.\\d{1,6})?|180(\\.0{1,6})?)$",
            message = "Долгота должна быть между -180 и 180 и до 6 знаков после запятой"
    )
    @Schema(description = "Долгота", example = "14.345678", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String longitude;
}
