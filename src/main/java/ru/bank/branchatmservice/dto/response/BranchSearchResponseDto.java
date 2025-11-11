package ru.bank.branchatmservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.branchatmservice.dto.AddressShortDto;
import ru.bank.branchatmservice.dto.BranchFullDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BranchSearchResponseDto {
    private BranchFullDto branchFullDto;
    private AddressShortDto addressShortDto;
}
