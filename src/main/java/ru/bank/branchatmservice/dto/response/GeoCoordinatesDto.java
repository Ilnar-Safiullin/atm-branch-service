package ru.bank.branchatmservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeoCoordinatesDto {

    @Schema(description = "Широта", example = "34.456789")
    private String latitude;

    @Schema(description = "Долгота", example = "14.345678")
    private String longitude;
}
