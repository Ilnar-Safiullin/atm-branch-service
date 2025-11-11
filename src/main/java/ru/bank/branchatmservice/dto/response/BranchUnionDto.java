package ru.bank.branchatmservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.branchatmservice.dto.ScheduleDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BranchUnionDto {
    private BranchDtoView branchInfo;
    private List<ScheduleDto> workSchedule;
}
