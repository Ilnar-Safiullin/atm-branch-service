package ru.bank.branchatmservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.bank.branchatmservice.dto.AddressShortDto;
import ru.bank.branchatmservice.dto.request.AddressFullInfoDto;
import ru.bank.branchatmservice.dto.response.AddressDto;
import ru.bank.branchatmservice.dto.response.GeoCoordinatesDto;
import ru.bank.branchatmservice.model.Address;


@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(target = "cityName", qualifiedByName = "cityPrefix", source = "city.name")
    @Mapping(source = "streetType", target = "streetType")
    @Mapping(source = "street", target = "street")
    @Mapping(target = "house", qualifiedByName = "housePrefix", source = "house")
    AddressShortDto toAddressShortDto(Address address);

    @Named("cityPrefix")
    default String addCityPrefix(String cityName) {
        if (cityName == null || cityName.trim().isEmpty()) {
            return null;
        }
        return "г. " + cityName.trim();
    }

    @Named("housePrefix")
    default String addHousePrefix(String house) {
        if (house == null || house.trim().isEmpty()) {
            return null;
        }
        return "д. " + house.trim();
    }

    @Named("toAddressFromAddressFullInfoDto")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "city", ignore = true)
    @Mapping(target = "fullAddress", expression = "java(generateFullAddress(addressFullInfoDto))")
    Address toAddress(AddressFullInfoDto addressFullInfoDto);

    @Named("toAddressDtoFromAddress")
    @Mapping(target = "cityName", qualifiedByName = "cityPrefix", source = "city.name")
    @Mapping(source = "streetType", target = "streetType")
    @Mapping(source = "street", target = "street")
    @Mapping(target = "house", qualifiedByName = "housePrefix", source = "house")
    @Mapping(target = "metroStation", source = "metroStation")
    @Mapping(target = "geoCoordinates", qualifiedByName = "toGeoCoordinatesFromAddress", source = "address")
    AddressDto toAddressDto(Address address);

    @Named("toGeoCoordinatesFromAddress")
    @Mapping(target = "latitude", expression = "java(address.getLatitude() != null ? address.getLatitude().toString() : null)")
    @Mapping(target = "longitude", expression = "java(address.getLongitude() != null ? address.getLongitude().toString() : null)")
    GeoCoordinatesDto toGeoCoordinatesDto(Address address);

    default String generateFullAddress(AddressFullInfoDto dto) {
        return String.format("%s %s %s %s",
                dto.cityName(),
                dto.streetType(),
                dto.street(),
                dto.house());
    }
}