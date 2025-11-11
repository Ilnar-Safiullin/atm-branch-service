package ru.bank.branchatmservice.dto;

import java.util.List;
import java.util.UUID;

public record BranchIdListDto(List<UUID> branchIds) {
}
