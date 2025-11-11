package ru.bank.branchatmservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.bank.branchatmservice.enums.BranchType;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchInfoUpdateDto {
    @Size(max = 64, message = "Название филиала не должно превышать 64 символов")
    @Pattern(regexp = "^[^a-zA-Z]*$", message = "Latin letters are not allowed")
    @Schema(description = "Наименование отделения", example = "ДО «ГУМ»", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String name;

    @Pattern(
            regexp = "^(0|[1-9]\\d{0,3})$",
            message = "The bank number must be from 0 to 9999"
    )
    @Schema(description = "Номер отделения", example = "101", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String bankNumber;

    @Pattern(
            regexp = "^\\+7\\d{10}$",
            message = "The phone number must be like +79999999999"
    )
    @Schema(description = "Мобильный номер телефона", example = "+79999999999", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String phoneNumber;

    @Schema(description = "Оказываются ли услуги по обмену валюты", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean hasCurrencyExchange;

    @Schema(description = "Оказываются ли услуги по обмену валюты", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean hasPandus;

    @Schema(description = "Закрыто ли отделение", example = "false", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean isClosed;

    @Schema(description = "Тип структурного подразделения", example = "BRANCH", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private BranchType type;
}
