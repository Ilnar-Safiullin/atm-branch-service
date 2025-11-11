package ru.bank.branchatmservice.dto.request;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.bank.branchatmservice.dto.ScheduleDto;

import java.util.List;

public record AtmCreateDto(
        @JsonProperty("atmInfo")
        AtmInfoCreateDto atmInfoCreateDto,

        @JsonProperty("addressInfo")
        AddressFullInfoDto addressFullInfoDto,

        @JsonProperty("branchInfo")
        BranchShortInfo branchShortInfo,

        List<ScheduleDto> scheduleArray
) {
}
