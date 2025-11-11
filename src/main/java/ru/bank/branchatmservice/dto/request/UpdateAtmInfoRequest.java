package ru.bank.branchatmservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.bank.branchatmservice.dto.ScheduleDto;
import ru.bank.branchatmservice.enums.Construction;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на обновление информации о банкомате")
public class UpdateAtmInfoRequest {
    @Schema(description = "Информация о банкомате")
    private UpdateAtmInfoDto atmInfo;
    @Schema(description = "Адресная информация")
    private UpdateAddressInfoDto addressInfo;
    @Schema(description = "Информация о расписании")
    private List<ScheduleDto> schedules;
    @Schema(description = "Информация о филиале")
    private UpdateBranchInfoDto branchInfo;

    @Schema(description = "Информация о банкомате")
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class UpdateAtmInfoDto {
        @Schema(description = "Инвентарный номер", example = "0001244890")
        @NotBlank
        private String inventoryNumber;

        @Schema(description = "Место установки", example = "Второй этаж, левое крыло")
        @NotBlank
        private String installationLocation;

        @Schema(description = "Тип конструкции", example = "Внешний")
        @NotBlank
        private Construction construction;

        @Schema(description = "Возможность внесения наличных", example = "true")
        @NotNull
        private Boolean cashDeposit;

        @Schema(description = "Наличие NFC", example = "true")
        @NotNull
        private Boolean nfc;
    }

    @Schema(description = "Адресная информация")
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class UpdateAddressInfoDto {
        @Schema(description = "Название города", example = "Москва")
        @NotBlank
        private String cityName;

        @Schema(description = "Тип улицы", example = "ал.")
        @NotBlank
        private String streetType;

        @Schema(description = "Название улицы", example = "Ленина")
        @NotBlank
        private String street;

        @Schema(description = "Номер дома", example = "143")
        @NotBlank
        private String house;

        @Schema(description = "Широта", example = "34.456789")
        @NotBlank
        private BigDecimal latitude;

        @Schema(description = "Долгота", example = "14.345678")
        @NotBlank
        private BigDecimal longitude;

        @Schema(description = "Станция метро", example = "Белорусская", requiredMode = Schema.RequiredMode.REQUIRED)
        private String metroStation;
    }

    @Schema(description = "Информация о филиале")
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class UpdateBranchInfoDto {
        @Schema(description = "Номер банка", example = "101")
        @NotBlank
        @Size(max = 4, message = "Длина номера банка не должна превышать 4 символа")
        private String bankNumber;
    }
}
