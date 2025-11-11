package ru.bank.branchatmservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Информация о банкомате")
public class InfoDeletionArchivingAtmResponse {

    @Schema(
            description = "Инвентарный номер банкомата",
            example = "1111000000"
    )
    String inventoryNumber;

    @Schema(
            description = "Название/локация банкомата",
            example = "Москва"
    )
    String name;

    @Schema(
            description = "Тип улицы",
            example = "ул."
    )
    String streetType;

    @Schema(
            description = "Название улицы",
            example = "Ленина"
    )
    String street;

    @Schema(
            description = "Номер дома",
            example = "173"
    )
    String house;
}
