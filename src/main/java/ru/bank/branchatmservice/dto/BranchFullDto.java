package ru.bank.branchatmservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Полные данные филиала банка")
public class BranchFullDto {
    @Schema(description = "Уникальный идентификатор филиала", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Название филиала", example = "Центральный офис")
    private String name;

    @JsonProperty("bank_number")
    @Schema(description = "Номер банка", example = "123456789")
    private String bankNumber;

    @JsonProperty("address_id")
    @Schema(description = "Идентификатор адреса филиала", example = "123e4567-e89b-12d3-a456-426614174001")
    private UUID addressId;

    @JsonProperty("has_currency_exchange")
    @Schema(description = "Доступна ли услуга обмена валют", example = "true")
    private boolean hasCurrencyExchange;

    @JsonProperty("phone_number")
    @Schema(description = "Номер телефона филиала", example = "+7-495-123-45-67")
    private String phoneNumber;

    @JsonProperty("has_pandus")
    @Schema(description = "Есть ли у филиала пандусы для инвалидов", example = "false")
    private boolean hasPandus;

    @JsonProperty("is_closed")
    @Schema(description = "Закрыт ли филиал", example = "false")
    private boolean isClosed;

    @JsonProperty("is_open")
    @Schema(description = "Открыт ли филиал в текущий момент", example = "true")
    private boolean isOpen;
}
