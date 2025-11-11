package ru.bank.branchatmservice.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.bank.branchatmservice.dto.ATMFullDto;
import ru.bank.branchatmservice.dto.InfoAtmDto;
import ru.bank.branchatmservice.dto.request.UpdateAtmInfoRequest;
import ru.bank.branchatmservice.dto.request.AtmCreateDto;
import ru.bank.branchatmservice.dto.response.AtmFilterResponseDto;
import ru.bank.branchatmservice.model.ATM;
import ru.bank.branchatmservice.model.Branch;
import ru.bank.branchatmservice.model.WorkSchedule;

import java.util.List;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", uses = {AddressMapper.class, WorkScheduleMapper.class})
public interface ATMMapper {
    @Mapping(target = "atmInfo.status",  source = "atm.closed")
    @Mapping(target = "atmInfo.address",  source = "atm.address")
    @Mapping(target = "atmInfo.geoCoordinates",  source = "atm.address")
    @Mapping(target = "atmInfo.metroStation",
            expression = "java(aTM.getAddress().getMetroStation() != null ? aTM.getAddress().getMetroStation() : null)")
    @Mapping(target = "atmInfo.number",  source = "atm.number")
    @Mapping(target = "atmInfo.installationLocation",  source = "atm.installationLocation")
    @Mapping(target = "atmInfo.construction",  source = "atm.construction")
    @Mapping(target = "atmInfo.cashDeposit",  source = "atm.hasCashDeposit")
    @Mapping(target = "atmInfo.nfc",  source = "atm.hasNfc")
    ATMFullDto ofATMBranchAndWorkSchedule(ATM atm, Branch branchInfo, List<WorkSchedule> schedule);

    @Mapping(target = "cityName", source = "address.city.name")
    @Mapping(target = "streetType", source = "address.streetType")
    @Mapping(target = "street", source = "address.street")
    @Mapping(target = "house", source = "address.house")
    InfoAtmDto toInfoAtmDto(ATM atm);

    @Mapping(target = "atmInfo.inventoryNumber",  source = "atm.inventoryNumber")
    @Mapping(target = "atmInfo.construction",  source = "atm.construction")
    @Mapping(target = "atmInfo.cashDeposit",  source = "atm.hasCashDeposit")
    @Mapping(target = "atmInfo.nfc",  source = "atm.hasNfc")
    @Mapping(target = "addressInfo.cityName",  source = "atm.address.city.name")
    @Mapping(target = "addressInfo.streetType",  source = "atm.address.streetType")
    @Mapping(target = "addressInfo.street",  source = "atm.address.street")
    @Mapping(target = "addressInfo.house",  source = "atm.address.house")
    @Mapping(target = "workSchedule", source = "atm.workSchedule")
    AtmFilterResponseDto toAtmFilterResponseDto(ATM atm);

    @Mapping(target = "number", source = "atmCreateDto.atmInfoCreateDto.ATMNumber")
    @Mapping(target = "inventoryNumber", source = "atmCreateDto.atmInfoCreateDto.inventoryNumber")
    @Mapping(target = "installationLocation", source = "atmCreateDto.atmInfoCreateDto.installationLocation")
    @Mapping(target = "construction", source = "atmCreateDto.atmInfoCreateDto.construction")
    @Mapping(target = "hasCashDeposit", source = "atmCreateDto.atmInfoCreateDto.cashDeposit")
    @Mapping(target = "hasNfc", source = "atmCreateDto.atmInfoCreateDto.nfc")
    @Mapping(target = "address", source = "atmCreateDto.addressFullInfoDto",
            qualifiedByName = "toAddressFromAddressFullInfoDto")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "branch", ignore = true)
    @Mapping(target = "isClosed", ignore = true)
    @Mapping(target = "workSchedule", ignore = true)
    ATM toATM(AtmCreateDto atmCreateDto);

    @Mapping(target = "inventoryNumber", source = "atmInfo.inventoryNumber")
    @Mapping(target = "installationLocation", source = "atmInfo.installationLocation")
    @Mapping(target = "construction", source = "atmInfo.construction")
    @Mapping(target = "hasCashDeposit", source = "atmInfo.cashDeposit")
    @Mapping(target = "hasNfc", source = "atmInfo.nfc")
    @Mapping(target = "address.city.name", source = "addressInfo.cityName")
    @Mapping(target = "address.streetType", source = "addressInfo.streetType")
    @Mapping(target = "address.street", source = "addressInfo.street")
    @Mapping(target = "address.house", source = "addressInfo.house")
    @Mapping(target = "address.latitude", source = "addressInfo.latitude")
    @Mapping(target = "address.longitude", source = "addressInfo.longitude")
    @Mapping(target = "address.metroStation", source = "addressInfo.metroStation")
    @Mapping(target = "branch.bankNumber", source = "branchInfo.bankNumber")
    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    ATM toATM(UpdateAtmInfoRequest atmDto, @MappingTarget ATM atm);
}
