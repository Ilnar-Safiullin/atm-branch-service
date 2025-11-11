package ru.bank.branchatmservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Название отделения")
public record BranchNameResponse(@Schema(description = "Название отделения", example = "ДО «ГУМ»")
                                 String branchName) {
}
