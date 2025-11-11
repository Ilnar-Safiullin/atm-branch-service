package ru.bank.branchatmservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BranchUpdateRequestDto {

    @Valid
    @NotNull(message = "Информация об отделении обязательна")
    @Schema(description = "Информация об отделении", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private BranchInfoUpdateDto branchInfo;

    @Valid
    @Schema(description = "Информация об адресе", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private AddressUpdateDto addressInfo;

    @Valid
    @Schema(description = "Информация о графике работы", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<WorkScheduleUpdateDto> workSchedule;
}
