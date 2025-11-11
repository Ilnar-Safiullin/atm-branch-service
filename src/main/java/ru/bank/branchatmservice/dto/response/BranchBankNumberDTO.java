package ru.bank.branchatmservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Schema(description = "DTO для представления номера отделения банка", example = "101")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchBankNumberDTO {
    @Schema(description = "Банковский номер отделения")
    private String bankNumber;
}