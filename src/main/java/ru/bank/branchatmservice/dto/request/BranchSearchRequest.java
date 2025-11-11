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
public class BranchSearchRequest {
    private BranchInfo branchInfo;
    private AddressInfo addressInfo;
}
