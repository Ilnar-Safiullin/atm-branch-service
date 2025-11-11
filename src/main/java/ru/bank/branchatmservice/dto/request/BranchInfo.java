package ru.bank.branchatmservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchInfo {
    private String nameOrBankNumber;
    private Boolean hasCurrencyExchange;
    private Boolean hasPandus;
    private Boolean isClosed;
}
