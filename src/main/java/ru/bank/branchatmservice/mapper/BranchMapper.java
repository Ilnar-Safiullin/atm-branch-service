package ru.bank.branchatmservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bank.branchatmservice.dto.BranchCreateInfoDto;
import ru.bank.branchatmservice.dto.BranchDto;
import ru.bank.branchatmservice.dto.BranchFullDto;
import ru.bank.branchatmservice.dto.response.ArchiveBranchResponse;
import ru.bank.branchatmservice.dto.response.BranchDtoView;
import ru.bank.branchatmservice.dto.request.AddressFullInfoDto;
import ru.bank.branchatmservice.dto.response.BranchListResponse;
import ru.bank.branchatmservice.dto.response.BranchSearchResponseDto;
import ru.bank.branchatmservice.model.Branch;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AddressMapper.class})
public interface BranchMapper {

    @Mapping(target = "branchFullDto", source = ".")
    @Mapping(target = "addressShortDto", source = "address")
    BranchSearchResponseDto toSearchResponseDto(Branch branch);

    @Mapping(target = "addressId", source = "address.id")
    @Mapping(target = "isClosed", source = "closed")
    @Mapping(target = "isOpen", ignore = true)
    BranchFullDto toBranchFullDto(Branch branch);

    @Mapping(target = "isOpen", ignore = true) // игнорируем, устанавливаем в сервисе
    @Mapping(target = "isClosed", source = "closed")
    BranchDto toBranchDto(Branch branch);

    List<BranchSearchResponseDto> toSearchResponseDtoList(List<Branch> branches);

    @Mapping(target = "hasCurrencyExchange", source = "hasCurrencyExchange")
    @Mapping(target = "hasPandus", source = "hasPandus")
    @Mapping(target = "isClosed", source = "closed")
    @Mapping(target = "address", source = "address",
            qualifiedByName = "toAddressDtoFromAddress")
    BranchDtoView toBranchDtoView(Branch branch);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "address", source = "address", qualifiedByName = "toAddressFromAddressFullInfoDto")
    Branch ofBranchCreateDto(BranchCreateInfoDto branch, AddressFullInfoDto address, Boolean isClosed);

    @Mapping(target = "branchInfo", source = "branch")
    @Mapping(target = "addressInfo", source = "branch.address")
    BranchListResponse ofBranch(Branch branch);

    List<BranchListResponse> ofBranches(List<Branch> branches);


    @Mapping(target = "branchId", source = "id")
    ArchiveBranchResponse.ArchiveBranchesDto toArchiveBranchResponse(Branch branch);
}