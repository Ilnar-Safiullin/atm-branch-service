package ru.bank.branchatmservice.service;

import org.junit.jupiter.api.BeforeEach;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import ru.bank.branchatmservice.dto.InfoAtmDto;
import ru.bank.branchatmservice.dto.request.AddressFullInfoDto;
import ru.bank.branchatmservice.dto.request.AtmCreateDto;

import static org.assertj.core.api.Assertions.assertThat;
import ru.bank.branchatmservice.dto.request.AtmFilterDto;
import ru.bank.branchatmservice.dto.request.AtmInfoCreateDto;
import ru.bank.branchatmservice.dto.request.BranchShortInfo;
import ru.bank.branchatmservice.dto.request.UpdateAtmInfoRequest;
import ru.bank.branchatmservice.dto.response.AtmFilterResponseDto;
import ru.bank.branchatmservice.dto.response.MessageResponseDto;
import ru.bank.branchatmservice.enums.Construction;
import ru.bank.branchatmservice.exception.BranchNotFoundException;
import ru.bank.branchatmservice.exception.CityNotFoundException;
import ru.bank.branchatmservice.exception.NotFoundException;
import ru.bank.branchatmservice.dto.ScheduleDto;
import ru.bank.branchatmservice.enums.EntityType;
import ru.bank.branchatmservice.enums.WeekDay;
import ru.bank.branchatmservice.mapper.ATMMapper;
import ru.bank.branchatmservice.mapper.AddressMapper;
import ru.bank.branchatmservice.mapper.WorkScheduleMapper;
import ru.bank.branchatmservice.model.ATM;
import ru.bank.branchatmservice.model.Address;
import ru.bank.branchatmservice.model.Branch;
import ru.bank.branchatmservice.model.City;
import ru.bank.branchatmservice.model.WorkSchedule;
import ru.bank.branchatmservice.repository.ATMRepository;
import ru.bank.branchatmservice.repository.WorkScheduleRepository;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ATMServiceTest {
    @Mock
    private BranchService branchService;

    @Mock
    private CityService cityService;

    @Mock
    private ATMRepository atmRepository;

    @Mock
    private WorkScheduleRepository workScheduleRepository;

    @Spy
    private ATMMapper atmMapper = Mappers.getMapper(ATMMapper.class);
    @Spy
    private WorkScheduleMapper workScheduleMapper = Mappers.getMapper(WorkScheduleMapper.class);

    @Captor
    ArgumentCaptor<List<WorkSchedule>> workSchedulesCaptor;

    @InjectMocks
    private ATMService atmService;

    UpdateAtmInfoRequest updateAtmInfoRequest;
    ATM atm;
    List<WorkSchedule> workSchedules;
    List<WorkSchedule> newWorkSchedules;

    @BeforeEach
    void setUp() {
        org.springframework.test.util.ReflectionTestUtils.setField(
                atmMapper,
                "workScheduleMapper",
                workScheduleMapper
        );
        org.springframework.test.util.ReflectionTestUtils.setField(
                atmMapper,
                "addressMapper",
                Mappers.getMapper(AddressMapper.class)
        );

        updateAtmInfoRequest = new UpdateAtmInfoRequest();

        UpdateAtmInfoRequest.UpdateAtmInfoDto atmInfo = new UpdateAtmInfoRequest.UpdateAtmInfoDto();
        atmInfo.setInventoryNumber("0001244890");
        atmInfo.setInstallationLocation("Второй этаж");
        atmInfo.setConstruction(Construction.EXTERNAL);
        atmInfo.setCashDeposit(true);
        atmInfo.setNfc(true);
        updateAtmInfoRequest.setAtmInfo(atmInfo);

        UpdateAtmInfoRequest.UpdateAddressInfoDto addressInfo = new UpdateAtmInfoRequest.UpdateAddressInfoDto();
        addressInfo.setCityName("Москва");
        addressInfo.setStreetType("ул.");
        addressInfo.setStreet("Ленина");
        addressInfo.setHouse("143");
        addressInfo.setLatitude(BigDecimal.valueOf(55.7558));
        addressInfo.setLongitude(BigDecimal.valueOf(37.6173));
        addressInfo.setMetroStation("Белорусская");
        updateAtmInfoRequest.setAddressInfo(addressInfo);

        UpdateAtmInfoRequest.UpdateBranchInfoDto branchInfo = new UpdateAtmInfoRequest.UpdateBranchInfoDto();
        branchInfo.setBankNumber("101");
        updateAtmInfoRequest.setBranchInfo(branchInfo);

        List<ScheduleDto> schedules = Arrays.asList(
                new ScheduleDto(1, "09:00", "18:00"),
                new ScheduleDto(2, "09:00", "18:00")
        );

        atm = new ATM();
        atm.setId(UUID.randomUUID());
        atm.setInventoryNumber("000111222");
        atm.setInstallationLocation("Старое местоположение");
        updateAtmInfoRequest.setSchedules(schedules);

        workSchedules = Arrays.asList(
                new WorkSchedule(UUID.randomUUID(), EntityType.ATM, atm.getId(),
                        WeekDay.MONDAY, LocalTime.parse(schedules.get(0).openingTime()),
                        LocalTime.parse(schedules.get(0).closingTime())),
                new WorkSchedule(UUID.randomUUID(), EntityType.ATM, atm.getId(),
                        WeekDay.TUESDAY, LocalTime.parse(schedules.get(1).openingTime()),
                        LocalTime.parse(schedules.get(1).closingTime()))
        );

        atm.setWorkSchedule(workSchedules);

        newWorkSchedules =  Arrays.asList(
                new WorkSchedule(null, null, null, WeekDay.MONDAY,
                        LocalTime.parse("09:00"), LocalTime.parse("18:00")),
                new WorkSchedule(null, null, null, WeekDay.TUESDAY,
                        LocalTime.parse("09:00"), LocalTime.parse("18:00"))
        );
    }

    private AtmCreateDto createTestAtmCreateDto() {
        AtmInfoCreateDto atmInfo = new AtmInfoCreateDto(
                "1111",
                "0001244890",
                "Второй этаж",
                "Внешний",
                true,
                true
        );

        AddressFullInfoDto addressInfo = new AddressFullInfoDto(
                "Москва",
                "ал.",
                "Ленина",
                "143",
                "34.456789",
                "14.345678",
                "Белорусская"
        );

        BranchShortInfo branchInfo = new BranchShortInfo("101");

        List<ScheduleDto> schedule = Arrays.asList(
                new ScheduleDto(1, "08:00", "19:00"),
                new ScheduleDto(2, "09:00", "20:00")
        );

        return new AtmCreateDto(atmInfo, addressInfo, branchInfo, schedule);
    }

    @Test
    void softDeleteATMByIds_ShouldReturnSuccessResponse_200() {
        UUID uuid1 = atm.getId();
        UUID uuid2 = UUID.fromString("6ba7b810-9dad-11d1-80b4-00c04fd430c8");
        List<UUID> atmIds = List.of(uuid1, uuid2);

        ATM atm2 = ATM.builder()
                .id(uuid2)
                .inventoryNumber("ATM-002")
                .address(Address.builder()
                        .city(new City(null, null, "СПб", true))
                        .streetType("пр.")
                        .street("Невский")
                        .house("25")
                        .build())
                .build();
        List<ATM> atms = List.of(atm, atm2);

        when(atmRepository.softDeleteByAtmIds(atmIds)).thenReturn(atms);

        List<InfoAtmDto> infoAtmDtos = atmService.softDeleteATMByIds(atmIds);

        assertNotNull(infoAtmDtos);
        assertEquals(2, infoAtmDtos.size());
        assertEquals(atms.get(0).getInventoryNumber(), infoAtmDtos.get(0).getInventoryNumber());
        assertEquals(atms.get(1).getInventoryNumber(), infoAtmDtos.get(1).getInventoryNumber());

        verify(atmRepository, times(1)).softDeleteByAtmIds(atmIds);
        verify(atmMapper, times(2)).toInfoAtmDto(any(ATM.class));
    }

    @Test
    void softDeleteATMByIds_SingleATMCorrectly_200() {
        List<UUID> atmIds = List.of(atm.getId());
        List<ATM> atms = List.of(atm);

        when(atmRepository.softDeleteByAtmIds(atmIds)).thenReturn(atms);

        List<InfoAtmDto> infoAtmDtos = atmService.softDeleteATMByIds(atmIds);

        assertNotNull(infoAtmDtos);
        assertEquals(1, infoAtmDtos.size());

        verify(atmRepository, times(1)).softDeleteByAtmIds(atmIds);
        verify(atmMapper, times(1)).toInfoAtmDto(any(ATM.class));
    }

    @Test
    void createATM_Success() {
        // Подготовка данных
        AtmCreateDto dto = createTestAtmCreateDto();
        Branch branch = new Branch();
        City city = new City();
        ATM atm = new ATM();
        Address address = new Address();
        atm.setAddress(address);
        List<WorkSchedule> schedules = Arrays.asList(new WorkSchedule(), new WorkSchedule());
        atm.setWorkSchedule(schedules);

        // Настройка моков
        when(branchService.findBranchByBankNumber("101")).thenReturn(branch);
        when(cityService.getCityByName("Москва")).thenReturn(city);
        when(atmMapper.toATM(dto)).thenReturn(atm);
        when(workScheduleMapper.toListWorkSchedules(any())).thenReturn(schedules);
        when(atmRepository.saveAndFlush(atm)).thenReturn(atm);
        when(atmRepository.existsByNumber(atm.getNumber())).thenReturn(false);
        when(workScheduleRepository.saveAll(schedules)).thenReturn(schedules);

        // Вызов метода
        MessageResponseDto response = atmService.createATM(dto);

        // Проверки
        assertNotNull(response);
        assertTrue(response.message().contains("успешно"));
        verify(atmRepository, times(1)).saveAndFlush(atm);
        verify(workScheduleRepository, times(1)).saveAll(schedules);
        assertEquals(branch, atm.getBranch());
        assertEquals(city, atm.getAddress().getCity());
    }

    @Test
    void createATM_BranchNotFound() {
        // Подготовка данных
        AtmCreateDto dto = createTestAtmCreateDto();
        ATM atm = new ATM();

        // Настройка моков
        when(branchService.findBranchByBankNumber("101"))
                .thenThrow(new BranchNotFoundException("Отделение не найдено"));
        when(atmMapper.toATM(dto)).thenReturn(atm);
        when(atmRepository.existsByNumber(any())).thenReturn(false);

        // Проверка исключения
        assertThrows(BranchNotFoundException.class, () -> atmService.createATM(dto));
        verify(atmRepository, never()).saveAndFlush(any());
    }

    @Test
    void createATM_CityNotFound() {
        // Подготовка данных
        AtmCreateDto dto = createTestAtmCreateDto();
        Branch branch = new Branch();
        ATM atm = new ATM();

        // Настройка моков
        when(atmMapper.toATM(dto)).thenReturn(atm);
        when(atmRepository.existsByNumber(any())).thenReturn(false);
        when(branchService.findBranchByBankNumber("101")).thenReturn(branch);
        when(cityService.getCityByName("Москва"))
                .thenThrow(new CityNotFoundException("Город не найден"));

        // Проверка исключения
        assertThrows(CityNotFoundException.class, () -> atmService.createATM(dto));
        verify(atmRepository, never()).saveAndFlush(any());
    }

    @Test
    void createATM_ATMExist() {
        // Подготовка данных
        AtmCreateDto dto = createTestAtmCreateDto();
        Branch branch = new Branch();
        ATM atm = new ATM();

        // Настройка моков
        when(atmMapper.toATM(dto)).thenReturn(atm);
        when(atmRepository.existsByNumber(any())).thenReturn(true);

        // Проверка исключения
        assertThrows(EntityExistsException.class, () -> atmService.createATM(dto));
        verify(atmRepository, never()).saveAndFlush(any());
    }

    @Test
    void EmptyAtmsList_whenGetAllATMs(){

        when(atmRepository.findAll()).thenReturn(Collections.emptyList());

        List<AtmFilterResponseDto> atm = atmService.getAllAtmList();

        assertThat(atm).isEmpty();
        assertEquals(0, atm.size());
    }

    @Test
    void AtmsList_whenGetAllATMs(){
        when(atmRepository.findAll()).thenReturn(List.of(atm));

        List<AtmFilterResponseDto> atm = atmService.getAllAtmList();

        Assertions.assertEquals(1,atm.size());
    }

    @Test
    void updateATM_WhenValidRequest_ShouldUpdateAtmAndWorkSchedules() {
        UpdateAtmInfoRequest request = updateAtmInfoRequest;
        ATM existingAtm = atm;
        List<WorkSchedule> newSchedules = newWorkSchedules;
        for (WorkSchedule workSchedule : newSchedules) {
            workSchedule.setEntityId(atm.getId());
            workSchedule.setEntityType(EntityType.ATM);
        }

        when(atmRepository.findById(atm.getId())).thenReturn(Optional.of(existingAtm));

        atmService.updateATM(atm.getId(), request);

        verify(atmRepository, times(1)).findById(atm.getId());
        verify(workScheduleRepository, times(1)).deleteAllByEntityIdAndEntityType(atm.getId(), EntityType.ATM);
        verify(atmMapper, times(1)).toATM(eq(request), eq(existingAtm));
        verify(atmRepository, times(1)).save(existingAtm);
        verify(workScheduleMapper, times(1)).ofWorkScheduleDtoList(eq(request.getSchedules()), any(List.class));
        verify(workScheduleRepository, times(1)).saveAll(workSchedulesCaptor.capture());
        assertEquals(newSchedules, workSchedulesCaptor.getValue());
    }

    @Test
    void updateATM_WhenAtmNotFound_ShouldThrowNotFoundException() {
        UUID nonExistentAtmId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        UpdateAtmInfoRequest request = updateAtmInfoRequest;

        when(atmRepository.findById(nonExistentAtmId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> atmService.updateATM(nonExistentAtmId, request));

        assertEquals("Данные не найдены.", exception.getMessage());

        verify(atmRepository, never()).save(any());
        verify(workScheduleRepository, never()).deleteAll(any());
        verify(workScheduleRepository, never()).saveAll(any());
        verify(atmMapper, never()).toATM(any(), any());
        verify(workScheduleMapper, never()).ofWorkScheduleDtoList(anyList(), anyList());
    }

    @Test
    @DisplayName("Получение непустого списка банкоматов по фильтру")
    void getAtmsByFilter_NotEmpty() {
        AtmFilterDto filters = new AtmFilterDto(
                "12345",
                true,
                true,
                true,
                true,
                "Москва",
                "ул",
                "Ленина",
                "1"
        );
        ATM atm2 = new ATM();
        atm2.setId(UUID.randomUUID());

        newWorkSchedules.forEach(schedule -> {
            schedule.setEntityId(atm2.getId());
            schedule.setEntityType(EntityType.ATM);});
        atm2.setWorkSchedule(newWorkSchedules);

        List<ATM> mockAtms = Arrays.asList(atm, atm2);

        when(atmRepository.findAll(any(Specification.class))).thenReturn(mockAtms);

        List<AtmFilterResponseDto> result = atmService.getAtmListByFilter(filters);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(atmRepository).findAll(any(Specification.class));
        verify(atmMapper, times(2)).toAtmFilterResponseDto(any(ATM.class));
    }

    @Test
    @DisplayName("Получение пустого списка банкоматов по фильтру")
    void getAtmsByFilter_Empty() {
        AtmFilterDto filters = new AtmFilterDto(
                "12345",
                true,
                true,
                true,
                true,
                "Москва",
                "ул",
                "Ленина",
                "1"
        );

        when(atmRepository.findAll(any(Specification.class))).thenReturn(List.of());

        List<AtmFilterResponseDto> result = atmService.getAtmListByFilter(filters);

        assertNotNull(result);
        assertEquals(0, result.size());
        assertTrue(result.isEmpty());
        verify(atmRepository).findAll(any(Specification.class));
        verify(workScheduleRepository, never()).findAll(any(Specification.class));
        verify(atmMapper, never()).toAtmFilterResponseDto(any(ATM.class));
    }
}