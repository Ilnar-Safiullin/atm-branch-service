package ru.bank.branchatmservice.dto.response;

import ru.bank.branchatmservice.dto.AddressShortDto;
import ru.bank.branchatmservice.dto.BranchDto;

public record BranchListResponse(
        BranchDto branchInfo,
        AddressShortDto addressInfo
) {
}
