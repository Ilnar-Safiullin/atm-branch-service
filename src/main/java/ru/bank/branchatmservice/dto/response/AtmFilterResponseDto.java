package ru.bank.branchatmservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.bank.branchatmservice.dto.AddressShortDto;
import ru.bank.branchatmservice.dto.ScheduleDto;

import java.util.List;

public record AtmFilterResponseDto(AtmInfo atmInfo, AddressShortDto addressInfo, List<ScheduleDto> workSchedule) {

    public record AtmInfo(
            @Schema(description = "Порядковый номер банкомата", example = "11")
            String inventoryNumber,

            @Schema(description = "Тип конструкции банкомата", example = "Внешний")
            String construction,

            @Schema(description = "Внесение наличных", example = "true")
            Boolean cashDeposit,

            @Schema(description = "Бесконтактная оплата", example = "true")
            Boolean nfc
    ) {}
}
