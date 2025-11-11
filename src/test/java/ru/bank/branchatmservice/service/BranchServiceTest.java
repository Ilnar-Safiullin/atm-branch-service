package ru.bank.branchatmservice.service;


import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import ru.bank.branchatmservice.dto.AddressShortDto;
import ru.bank.branchatmservice.dto.BranchFullDto;
import ru.bank.branchatmservice.dto.BranchShortDto;
import ru.bank.branchatmservice.dto.BranchShortDtoProjection;
import ru.bank.branchatmservice.dto.ScheduleDto;
import ru.bank.branchatmservice.dto.request.AddressInfo;
import ru.bank.branchatmservice.dto.request.BranchAndAddressInfoRequest;
import ru.bank.branchatmservice.dto.request.BranchInfo;
import ru.bank.branchatmservice.dto.request.BranchSearchRequest;
import ru.bank.branchatmservice.dto.response.ArchiveBranchResponse;
import ru.bank.branchatmservice.dto.response.BranchAndAddressInfoResponse;
import ru.bank.branchatmservice.dto.response.BranchBankNumberDTO;
import ru.bank.branchatmservice.dto.response.BranchDtoView;
import ru.bank.branchatmservice.dto.response.BranchSearchResponseDto;
import ru.bank.branchatmservice.dto.response.BranchUnionDto;
import ru.bank.branchatmservice.enums.WeekDay;
import ru.bank.branchatmservice.exception.BranchNotFoundException;
import ru.bank.branchatmservice.exception.NotFoundException;
import ru.bank.branchatmservice.mapper.AddressMapper;
import ru.bank.branchatmservice.mapper.BranchMapper;
import ru.bank.branchatmservice.mapper.WorkScheduleMapper;
import ru.bank.branchatmservice.model.Address;
import ru.bank.branchatmservice.model.Branch;
import ru.bank.branchatmservice.model.City;
import ru.bank.branchatmservice.model.WorkSchedule;
import ru.bank.branchatmservice.repository.BranchRepository;


import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchServiceTest {

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private WorkScheduleService workScheduleService;

    @Spy
    private BranchMapper branchMapper = Mappers.getMapper(BranchMapper.class);

    @Spy
    private AddressMapper addressMapper = Mappers.getMapper(AddressMapper.class);

    @Spy
    private WorkScheduleMapper workScheduleMapper = Mappers.getMapper(WorkScheduleMapper.class);

    @InjectMocks
    private BranchService branchService;


    private final UUID BRANCH_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private final UUID WORK_SCHEDULE_ID = UUID.fromString("223e4567-e89b-12d3-a456-426614174000");

    @BeforeEach
    void setUp() {
        org.springframework.test.util.ReflectionTestUtils.setField(
                branchMapper,
                "addressMapper",
                addressMapper
        );
    }

    private Branch createTestBranch() {
        return Branch.builder()
                .id(BRANCH_ID)
                .name("ДО «ГУМ»")
                .bankNumber("101")
                .phoneNumber("78478579955")
                .hasCurrencyExchange(true)
                .hasPandus(true)
                .isClosed(false)
                .workSchedule(createTestWorkSchedules())
                .build();
    }

    private List<WorkSchedule> createTestWorkSchedules() {
        WorkSchedule schedule = WorkSchedule.builder()
                .id(WORK_SCHEDULE_ID)
                .entityId(BRANCH_ID)
                .weekDay(WeekDay.MONDAY)
                .openingTime(LocalTime.of(9, 30))
                .closingTime(LocalTime.of(18, 30))
                .build();
        return List.of(schedule);
    }

    private BranchDtoView createTestBranchDtoView() {
        BranchDtoView dto = new BranchDtoView();
        dto.setName("ДО «ГУМ»");
        dto.setBankNumber("101");
        dto.setPhoneNumber("78478579955");
        dto.setHasCurrencyExchange(true);
        dto.setHasPandus(true);
        dto.setIsClosed(true);
        return dto;
    }

    private List<ScheduleDto> createTestScheduleDtos() {
        ScheduleDto scheduleDto = new ScheduleDto(1, "08:00", "19:00");
        return List.of(scheduleDto);
    }

    @Test
    void searchBranchesByFilter_200() {
        BranchInfo branchInfo = BranchInfo.builder()
                .nameOrBankNumber("BranchName 1111")
                .hasCurrencyExchange(true)
                .hasPandus(true)
                .isClosed(false)
                .build();

        AddressInfo addressInfo = AddressInfo.builder()
                .fullAddress("CityNameTest ул. Тестовая")
                .build();

        BranchSearchRequest request = new BranchSearchRequest(branchInfo, addressInfo);

        City city = City.builder()
                .id(UUID.randomUUID())
                .name("TestCity")
                .build();

        Address address = Address.builder()
                .id(UUID.randomUUID())
                .city(city)
                .streetType("ул.")
                .street("Тестовая")
                .house("10")
                .fullAddress("г. TestCity ул. Тестовая д. 10")
                .build();

        Branch branch = Branch.builder()
                .id(UUID.randomUUID())
                .name("BranchName")
                .isClosed(false)
                .address(address)
                .workSchedule(new ArrayList<>()) // добавляем пустой список workSchedule
                .build();

        // Мокаем результаты маппинга
        BranchFullDto branchFullDto = BranchFullDto.builder()
                .id(branch.getId())
                .name(branch.getName())
                .isClosed(false)
                .build();

        AddressShortDto addressShortDto = AddressShortDto.builder()
                .cityName("г. TestCity")
                .streetType("ул.")
                .street("Тестовая")
                .house("д. 10")
                .build();

        when(branchRepository.findAll(any(Specification.class))).thenReturn(List.of(branch));

        doReturn(branchFullDto).when(branchMapper).toBranchFullDto(branch);
        doReturn(addressShortDto).when(addressMapper).toAddressShortDto(address);

        when(workScheduleService.isBranchOpenNow(any())).thenReturn(true);

        List<BranchSearchResponseDto> result = branchService.searchBranchesByFilter(request);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        BranchSearchResponseDto responseDto = result.get(0);
        assertTrue(responseDto.getBranchFullDto().isOpen());

        verify(branchRepository, times(1)).findAll(any(Specification.class));
        verify(branchMapper, times(1)).toBranchFullDto(branch);
        verify(addressMapper, times(1)).toAddressShortDto(address);
        verify(workScheduleService, times(1)).isBranchOpenNow(branch.getWorkSchedule());
    }

    @Test
    void getBranchAndAddressInfo_WhenBranchExists_ShouldReturnResponse(){
        String bankNumber = "123456";
        BranchAndAddressInfoRequest request = new BranchAndAddressInfoRequest(bankNumber);

        Address address = new Address();
        address.setId(UUID.randomUUID());
        City city =new City();
        city.setId(UUID.randomUUID());
        address.setCity(city);
        address.setStreet("street");
        address.setStreet("Tverskaya");

        Branch branch = new Branch();
        branch.setId(UUID.randomUUID());
        branch.setBankNumber("123456");
        branch.setName("Test Branch");
        branch.setAddress(address);

        BranchFullDto expectedBranchDto = new BranchFullDto();
        AddressShortDto expectedAddressDto = new AddressShortDto();
        expectedAddressDto.setStreet(address.getStreet());
        expectedAddressDto.setCityName(city.getName());
        address.setStreetType(address.getStreetType());
        address.setId(address.getId());

        when(branchRepository.findByBankNumber(bankNumber))
                .thenReturn(Optional.of(branch));
        doReturn(expectedBranchDto)
                .when(branchMapper).toBranchFullDto(branch);
        doReturn(expectedAddressDto)
                .when(addressMapper).toAddressShortDto(address);

        BranchAndAddressInfoResponse response = branchService.getBranchAndAddressInfo(request);

        assertNotNull(response);
        assertEquals(expectedBranchDto, response.getBranchInfo());
        assertEquals(expectedAddressDto, response.getAddressInfo());

        verify(branchRepository, times(1)).findByBankNumber(bankNumber);
        verify(branchMapper, times(1)).toBranchFullDto(branch);
        verify(addressMapper, times(1)).toAddressShortDto(address);
    }

    @Test
    void getBranchAndAddressInfo_WhenBranchNotFound_ShouldThrowNotFoundException() {
        String bankNumber = "999999";
        BranchAndAddressInfoRequest request = new BranchAndAddressInfoRequest(bankNumber);

        when(branchRepository.findByBankNumber(bankNumber))
                .thenReturn(Optional.empty());

        BranchNotFoundException exception = assertThrows(BranchNotFoundException.class, () -> {
            branchService.getBranchAndAddressInfo(request);
        });

        assertEquals("Данные не найдены", exception.getMessage());
        verify(branchRepository, times(1)).findByBankNumber(bankNumber);
        verify(branchMapper, never()).toBranchFullDto(any());
        verify(addressMapper, never()).toAddressShortDto(any());
    }

    @Test
    @DisplayName("Должен вернуться список отделений при корректном Id отдела")
    void shouldReturnShortBranches_ifDepartmentIdValid() {
        UUID deptId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();
        String branchName = "ДО «ГУМ»";

        BranchShortDtoProjection branchProjection = mock(BranchShortDtoProjection.class);
        when(branchProjection.getId()).thenReturn(branchId);
        when(branchProjection.getName()).thenReturn(branchName);
        when(branchRepository.findBranchesByDepartmentId(deptId))
                .thenReturn(List.of(branchProjection));

        List<BranchShortDto> branches = branchService.findBranchesByDepartmentId(deptId);

        verify(branchRepository, times(1)).findBranchesByDepartmentId(deptId);
        assertEquals(branchId, branches.get(0).bankBranchId());
        assertEquals(branchName, branches.get(0).bankBranchName());
    }

    @Test
    void getBranchBankNumberByBankNumber_ShouldReturnList_WhenDataExists() {
        String bankNumberPrefix = "10";
        List<BranchBankNumberDTO> expectedDTOs = Arrays.asList(
                new BranchBankNumberDTO("101"),
                new BranchBankNumberDTO("102"),
                new BranchBankNumberDTO("103")
        );

        when(branchRepository.findBankNumberDTOsByPrefix(bankNumberPrefix)).thenReturn(expectedDTOs);

        List<BranchBankNumberDTO> result = branchService.getBranchBankNumberByBankNumber(bankNumberPrefix);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("101", result.get(0).getBankNumber());
        assertEquals("102", result.get(1).getBankNumber());
        assertEquals("103", result.get(2).getBankNumber());
        verify(branchRepository, times(1)).findBankNumberDTOsByPrefix(bankNumberPrefix);
    }

    @Test
    void getBranchBankNumberByBankNumber_ShouldThrowNotFoundException_WhenDataIsEmpty() {
        String bankNumberPrefix = "99";
        when(branchRepository.findBankNumberDTOsByPrefix(bankNumberPrefix)).thenReturn(Collections.emptyList());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> branchService.getBranchBankNumberByBankNumber(bankNumberPrefix));

        assertEquals("Данные не найдены", exception.getMessage());
        verify(branchRepository, times(1)).findBankNumberDTOsByPrefix(bankNumberPrefix);
    }

    @Test
    void getBranchBankNumberByBankNumber_ShouldLogInfoMessage() {
        String bankNumberPrefix = "10";
        List<BranchBankNumberDTO> expectedDTOs = Arrays.asList(
                new BranchBankNumberDTO("101"),
                new BranchBankNumberDTO("102")
        );

        when(branchRepository.findBankNumberDTOsByPrefix(bankNumberPrefix)).thenReturn(expectedDTOs);

        branchService.getBranchBankNumberByBankNumber(bankNumberPrefix);

        verify(branchRepository, times(1)).findBankNumberDTOsByPrefix(bankNumberPrefix);
    }

    @Test
    void getBranchBankNumberByBankNumber_ShouldReturnCorrectDTOs() {
        String bankNumberPrefix = "20";
        List<BranchBankNumberDTO> repositoryResponse = Arrays.asList(
                new BranchBankNumberDTO("201"),
                new BranchBankNumberDTO("202")
        );

        when(branchRepository.findBankNumberDTOsByPrefix(bankNumberPrefix)).thenReturn(repositoryResponse);

        List<BranchBankNumberDTO> result = branchService.getBranchBankNumberByBankNumber(bankNumberPrefix);

        assertSame(repositoryResponse, result); // Должен вернуть тот же объект что и репозиторий
        assertEquals(2, result.size());
    }

    @Test
    void findBranchById_WhenBranchExists_ShouldReturnBranchUnionDto() {
        // Given
        Branch branch = createTestBranch();
        BranchDtoView branchDtoView = createTestBranchDtoView();
        List<ScheduleDto> scheduleDtos = createTestScheduleDtos();

        when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.of(branch));
        doReturn(branchDtoView)
                .when(branchMapper).toBranchDtoView(branch);
        doReturn(scheduleDtos)
                .when(workScheduleMapper).ofWorkSchedules(branch.getWorkSchedule());

        // When
        BranchUnionDto result = branchService.findBranchById(BRANCH_ID);

        // Then
        assertNotNull(result);
        assertEquals(branchDtoView, result.getBranchInfo());
        assertEquals(scheduleDtos, result.getWorkSchedule());

        verify(branchRepository).findById(BRANCH_ID);
        verify(branchMapper).toBranchDtoView(branch);
        verify(workScheduleMapper).ofWorkSchedules(branch.getWorkSchedule());
    }


    @Test
    void findBranchById_WhenBranchNotFound_ShouldThrowBranchNotFoundException() {
        // Given
        when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.empty());

        // When & Then
        BranchNotFoundException exception = assertThrows(
                BranchNotFoundException.class,
                () -> branchService.findBranchById(BRANCH_ID)
        );

        assertEquals(String.format("Отделение с id %s не найдено", BRANCH_ID), exception.getMessage());
        verify(branchRepository).findById(BRANCH_ID);
//        verifyNoInteractions(workScheduleRepository, branchMapper, workScheduleMapper);
    }

    @Test
    void findBranchById_WhenWorkSchedulesEmpty_ShouldReturnBranchUnionDtoWithEmptySchedules() {
        // Given
        Branch branch = createTestBranch();
        BranchDtoView branchDtoView = createTestBranchDtoView();
        List<WorkSchedule> emptyWorkSchedules = List.of();
        List<ScheduleDto> emptyScheduleDtos = List.of();
        branch.setWorkSchedule(emptyWorkSchedules);

        when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.of(branch));
        when(branchMapper.toBranchDtoView(branch)).thenReturn(branchDtoView);
        when(workScheduleMapper.ofWorkSchedules(emptyWorkSchedules)).thenReturn(emptyScheduleDtos);

        // When
        BranchUnionDto result = branchService.findBranchById(BRANCH_ID);

        // Then
        assertNotNull(result);
        assertEquals(branchDtoView, result.getBranchInfo());
        assertTrue(result.getWorkSchedule().isEmpty());

        verify(branchRepository).findById(BRANCH_ID);
        verify(branchMapper).toBranchDtoView(branch);
        verify(workScheduleMapper).ofWorkSchedules(emptyWorkSchedules);
    }

    @Test
    void archiveBranchesId_WhenBranchHasSuchOpenedBranches_ShouldReturnArchiveResponse() throws BadRequestException {
        UUID branchId1 = UUID.randomUUID();
        UUID branchId2 = UUID.randomUUID();

       Branch branch1 = new Branch();
        branch1.setId(branchId1);
        branch1.setName("ДО «ГУМ»");
        branch1.setClosed(false);

        Branch branch2 = new Branch();
        branch2.setId(branchId2);
        branch2.setName("ДО «Центральный»");
        branch2.setClosed(false);

       ArchiveBranchResponse.ArchiveBranchesDto response1 = new ArchiveBranchResponse.ArchiveBranchesDto();
        response1.setBranchId(branchId1);
        response1.setName("ДО «ГУМ»");

        ArchiveBranchResponse.ArchiveBranchesDto response2 = new ArchiveBranchResponse.ArchiveBranchesDto();
        response2.setBranchId(branchId2);
        response2.setName("ДО «Центральный»");


        List<UUID> branchIds = List.of(branchId1, branchId2);
        List<Branch> branches = List.of(branch1, branch2);
        ArchiveBranchResponse expectedResponses = new ArchiveBranchResponse();

        expectedResponses.setArchived(List.of(response1, response2));
        when(branchRepository.findAllByBranchId(branchIds))
                .thenReturn(Optional.of(branches));
        when(branchMapper.toArchiveBranchResponse(branch1)).thenReturn(response1);
        when(branchMapper.toArchiveBranchResponse(branch2)).thenReturn(response2);

        ArchiveBranchResponse result = branchService.archiveBranchesId(branchIds);

        assertNotNull(result);
        assertEquals(2, result.getArchived().size());
        assertEquals(branchId1, result.getArchived().get(0).getBranchId());
        assertEquals(branchId2, result.getArchived().get(1).getBranchId());

        assertTrue(branch1.isClosed());
        assertTrue(branch2.isClosed());

        verify(branchRepository).saveAll(branches);
        verify(branchMapper).toArchiveBranchResponse(branch1);
        verify(branchMapper).toArchiveBranchResponse(branch2);
    }

    @Test
    void archiveBranchesId_WhenBranchNotFound_ShouldThrowNotFoundException() {
        UUID branchId1 = UUID.randomUUID();
        UUID branchId2 = UUID.randomUUID();

        List<UUID> branchIds = List.of(branchId1, branchId2);

        when(branchRepository.findAllByBranchId(branchIds))
                .thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> branchService.archiveBranchesId(branchIds));

        assertEquals("Такого отделения не существует", exception.getMessage());
        verify(branchRepository, never()).saveAll(anyList());
    }

    @Test
    void archiveBranchesId_WhenBranchAlreadyArchived_ShouldThrowEntityExistsException() {
        UUID branchId1 = UUID.randomUUID();

        Branch branch1 = new Branch();
        branch1.setId(branchId1);
        branch1.setName("ДО «ГУМ»");
        branch1.setClosed(false);

        List<UUID> branchIds = List.of(branchId1);
        branch1.setClosed(true);
        List<Branch> branches = List.of(branch1);

        when(branchRepository.findAllByBranchId(branchIds))
                .thenReturn(Optional.of(branches));

        EntityExistsException exception = assertThrows(EntityExistsException.class,
                () -> branchService.archiveBranchesId(branchIds));

        assertEquals("Отделение " + branchId1 + " уже находится в архиве.", exception.getMessage());
        verify(branchRepository, never()).saveAll(anyList());
    }

    @Test
    void archiveBranchesId_WhenBranchIsEmpty_ShouldThrowBadRequestException() {
        List<UUID> branchIds = List.of();

        assertThrows(BadRequestException.class,
                () -> branchService.archiveBranchesId(branchIds));
        verify(branchRepository, never()).findAllByBranchId(anyList());
        verify(branchRepository, never()).saveAll(anyList());
    }

    @Test
    void findBranchesByDepartmentIdReturnEmptyListIfNothingFound() {
        //given
        UUID uuid = UUID.randomUUID();
        List<BranchShortDtoProjection> shortDtoProjections = Collections.emptyList();
        List<BranchShortDto> shortDtoList = Collections.emptyList();

        when(branchRepository.findBranchesByDepartmentId(uuid)).thenReturn(shortDtoProjections);
        when(branchService.findBranchesByDepartmentId(uuid)).thenReturn(shortDtoList);

        //when
        List<BranchShortDto> result = branchService.findBranchesByDepartmentId(uuid);

        //then
        assertDoesNotThrow(() -> branchService.findBranchesByDepartmentId(uuid));
        assertTrue(result.isEmpty());

        verify(branchRepository, times(2)).findBranchesByDepartmentId(uuid);
    }

    @Test
    void searchBranchesByFilterReturnEmptyListIfNothingFound() {
        BranchSearchRequest bsr = new BranchSearchRequest(new BranchInfo(), new AddressInfo());
        when(branchRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

        List<BranchSearchResponseDto> result = branchService.searchBranchesByFilter(bsr);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(branchRepository, times(1)).findAll(any(Specification.class));
        verify(branchMapper, never()).toBranchFullDto(any());
        verify(addressMapper, never()).toAddressShortDto(any());
        verify(workScheduleService, never()).isBranchOpenNow(any());
    }
}
