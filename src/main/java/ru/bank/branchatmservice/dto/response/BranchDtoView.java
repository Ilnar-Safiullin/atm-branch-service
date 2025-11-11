package ru.bank.branchatmservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BranchDtoView {

    @Schema(description = "Название филиала", example = "Центральный офис")
    private String name;

    @Schema(description = "Номер банка", example = "123456789")
    private String bankNumber;

    @Schema(description = "Номер телефона филиала", example = "+74951234567")
    private String phoneNumber;

    @Schema(description = "Доступна ли услуга обмена валют", example = "true")
    private Boolean hasCurrencyExchange;

    @Schema(description = "Есть ли у филиала пандусы для инвалидов", example = "false")
    private Boolean hasPandus;

    @Schema(description = "Закрыт ли филиал", example = "false")
    private Boolean isClosed;

    private AddressDto address;
}
