package ru.bank.branchatmservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.bank.branchatmservice.dto.AddressShortDto;
import ru.bank.branchatmservice.dto.BranchFullDto;

@AllArgsConstructor
@Setter
@Getter
public class BranchAndAddressInfoResponse {
    @JsonProperty("branchInfo")
    private BranchFullDto branchInfo;

    @JsonProperty("addressInfo")
    private AddressShortDto addressInfo;
}
