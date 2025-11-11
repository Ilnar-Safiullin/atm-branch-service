package ru.bank.branchatmservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record BranchDto(
        @Schema(description = "Уникальный идентификатор филиала", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Название филиала", example = "Центральный офис")
        String name,

        @Schema(description = "Номер банка", example = "123456789")
        String bankNumber,

        @Schema(description = "Доступна ли услуга обмена валют", example = "true")
        boolean hasCurrencyExchange,

        @Schema(description = "Есть ли у филиала пандусы для инвалидов", example = "false")
        boolean hasPandus,

        @Schema(description = "Закрыт ли филиал", example = "false")
        boolean isClosed,

        @Schema(description = "Номер телефона филиала", example = "+78478579955")
        String phoneNumber,

        @Schema(description = "Открыт ли филиал в текущий момент", example = "true")
        boolean isOpen
) {
        public BranchDto withOpen(boolean isOpen) {
                return new BranchDto(
                        this.id,
                        this.name,
                        this.bankNumber,
                        this.hasCurrencyExchange,
                        this.hasPandus,
                        this.isClosed,
                        this.phoneNumber,
                        isOpen
                );
        }
}
