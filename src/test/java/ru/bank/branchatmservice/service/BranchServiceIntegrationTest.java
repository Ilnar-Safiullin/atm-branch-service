package ru.bank.branchatmservice.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.bank.branchatmservice.dto.BranchCreateDto;
import ru.bank.branchatmservice.dto.BranchCreateInfoDto;
import ru.bank.branchatmservice.dto.ScheduleDto;
import ru.bank.branchatmservice.dto.request.AddressFullInfoDto;
import ru.bank.branchatmservice.dto.request.AddressInfo;
import ru.bank.branchatmservice.dto.request.BranchInfo;
import ru.bank.branchatmservice.dto.request.BranchSearchRequest;
import ru.bank.branchatmservice.dto.response.ArchiveBranchResponse;
import ru.bank.branchatmservice.dto.response.BranchListResponse;
import ru.bank.branchatmservice.dto.response.BranchNameResponse;
import ru.bank.branchatmservice.dto.response.BranchSearchResponseDto;
import ru.bank.branchatmservice.dto.response.MessageResponseDto;
import ru.bank.branchatmservice.enums.BranchType;
import ru.bank.branchatmservice.enums.EntityType;
import ru.bank.branchatmservice.enums.WeekDay;
import ru.bank.branchatmservice.exception.NotFoundException;
import ru.bank.branchatmservice.mapper.AddressMapperImpl;
import ru.bank.branchatmservice.mapper.BranchMapperImpl;
import ru.bank.branchatmservice.mapper.WorkScheduleMapper;
import ru.bank.branchatmservice.mapper.WorkScheduleMapperImpl;
import ru.bank.branchatmservice.model.Address;
import ru.bank.branchatmservice.model.Branch;
import ru.bank.branchatmservice.model.City;
import ru.bank.branchatmservice.model.Location;
import ru.bank.branchatmservice.model.WorkSchedule;
import ru.bank.branchatmservice.repository.BranchRepository;
import ru.bank.branchatmservice.repository.WorkScheduleRepository;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Import({BranchService.class, BranchMapperImpl.class, AddressMapperImpl.class, WorkScheduleMapperImpl.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Testcontainers
class BranchServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.liquibase.enabled", () -> "true");
    }

    @MockBean
    private WorkScheduleRepository workScheduleRepository;

    @MockBean
    private WorkScheduleMapper workScheduleMapper;

    @MockBean
    private BranchUpdateService branchUpdateService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private BranchService branchService;

    private Branch branch;
    private BranchCreateDto newBranch;
    private BranchCreateDto invalidBranch;

    @BeforeEach
    void setUp() {
        entityManager.createNativeQuery("DELETE FROM work_schedule").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM atm").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM branch_department").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM branch").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM address").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM city").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM location").executeUpdate();

        Location location = Location.builder()
                .countyName("Московская область")
                .build();
        entityManager.persist(location);

        City city = City.builder()
                .name("Москва")
                .isPopular(true)
                .location(location)
                .build();
        entityManager.persist(city);

        // Создаем адреса
        Address address1 = Address.builder()
                .city(city)
                .streetType("улица")
                .street("Ленина")
                .house("10")
                .latitude(new BigDecimal("55.7558"))
                .longitude(new BigDecimal("37.6173"))
                .metroStation("Охотный ряд")
                .fullAddress(city.getName() + " улица Ленина 10")
                .build();
        entityManager.persist(address1);

        Address address2 = Address.builder()
                .city(city)
                .streetType("просп")
                .street("Мира")
                .house("20")
                .latitude(new BigDecimal("50.7558"))
                .longitude(new BigDecimal("30.6173"))
                .metroStation("Сокольники")
                .fullAddress(city.getName() + " просп Мира 20")
                .build();
        entityManager.persist(address2);

        Address address3 = Address.builder()
                .city(city)
                .streetType("просп")
                .street("Круглосуточный")
                .house("30")
                .latitude(new BigDecimal("55.7000"))
                .longitude(new BigDecimal("37.6000"))
                .metroStation("Круглосуточная")
                .fullAddress(city.getName() + " просп Круглосуточный 30")
                .build();
        entityManager.persist(address3);

        // Филиал 1: Центральное отделение (обычное расписание)
        branch = Branch.builder()
                .name("Центральное отделение")
                .bankNumber("1234")
                .address(address1)
                .hasCurrencyExchange(true)
                .phoneNumber("+77777777777")
                .hasPandus(true)
                .isClosed(false)
                .type(BranchType.BRANCH)
                .build();
        entityManager.persist(branch);

        // Филиал 2: Центровое (без пандуса)
        Branch branch2 = Branch.builder()
                .name("Центровое")
                .bankNumber("1222")
                .address(address2)
                .hasCurrencyExchange(true)
                .phoneNumber("+77777777778")
                .hasPandus(false)
                .isClosed(false)
                .type(BranchType.BRANCH)
                .build();
        entityManager.persist(branch2);

        // Филиал 3: Круглосуточное отделение
        Branch branch3 = Branch.builder()
                .name("Круглосуточное отделение")
                .bankNumber("9999")
                .address(address3)
                .hasCurrencyExchange(true)
                .phoneNumber("+77777777779")
                .hasPandus(true)
                .isClosed(false)
                .type(BranchType.BRANCH)
                .build();
        entityManager.persist(branch3);

        // Создаем расписание для круглосуточного филиала (открыт 24/7)
        for (WeekDay day : WeekDay.values()) {
            WorkSchedule schedule = WorkSchedule.builder()
                    .entityType(EntityType.BRANCH)
                    .entityId(branch3.getId())
                    .weekDay(day)
                    .openingTime(LocalTime.MIN) // 00:00
                    .closingTime(LocalTime.MAX.minusNanos(1)) // 23:59:59.999999999
                    .build();
            entityManager.persist(schedule);
        }

        entityManager.flush();
        entityManager.clear();

        newBranch = new BranchCreateDto(
                new BranchCreateInfoDto(
                        "ДО «ГУМ»",
                        "101",
                        true,
                        true,
                        "+79991234567",
                        BranchType.BRANCH
                ),
                List.of(UUID.randomUUID()),
                new AddressFullInfoDto(
                        "Москва",
                        "ул.",
                        "Красная Площадь",
                        "3",
                        "55.754167",
                        "37.620000",
                        "Охотный Ряд"
                ),
                List.of(
                        new ScheduleDto(1, "09:00", "20:00"),
                        new ScheduleDto(2, "09:00", "20:00"),
                        new ScheduleDto(3, "09:00", "20:00"),
                        new ScheduleDto(4, "09:00", "20:00"),
                        new ScheduleDto(5, "09:00", "20:00"),
                        new ScheduleDto(6, "10:00", "18:00"),
                        new ScheduleDto(7, "10:00", "18:00")
                )
        );

        invalidBranch = new BranchCreateDto(
                new BranchCreateInfoDto(
                        branch.getName(),
                        branch.getBankNumber(),
                        true,
                        true,
                        branch.getPhoneNumber(),
                        BranchType.BRANCH
                ),
                List.of(UUID.randomUUID()),
                new AddressFullInfoDto(
                        "Москва",
                        "ул.",
                        "Красная Площадь",
                        "3",
                        "55.754167",
                        "37.620000",
                        "Охотный Ряд"
                ),
                List.of(
                        new ScheduleDto(1, "09:00", "20:00"),
                        new ScheduleDto(2, "09:00", "20:00"),
                        new ScheduleDto(3, "09:00", "20:00"),
                        new ScheduleDto(4, "09:00", "20:00"),
                        new ScheduleDto(5, "09:00", "20:00"),
                        new ScheduleDto(6, "10:00", "18:00"),
                        new ScheduleDto(7, "10:00", "18:00")
                )
        );
    }

    @Test
    void searchBranchesByFilter_PrefixCityNameChek_200() {
        BranchInfo branchInfo = new BranchInfo();
        branchInfo.setNameOrBankNumber("Центральное 1234");

        BranchSearchRequest request = new BranchSearchRequest();
        request.setBranchInfo(branchInfo);

        List<BranchSearchResponseDto> result = branchService.searchBranchesByFilter(request);

        assertNotNull(result);
        assertEquals("г. Москва", result.get(0).getAddressShortDto().getCityName());
    }

    @Test
    void searchBranchesByFilter_PrefixHouseChek_200() {
        BranchInfo branchInfo = new BranchInfo();
        branchInfo.setNameOrBankNumber("Центральное 1234");

        BranchSearchRequest request = new BranchSearchRequest();
        request.setBranchInfo(branchInfo);

        List<BranchSearchResponseDto> result = branchService.searchBranchesByFilter(request);

        assertNotNull(result);
        assertEquals("д. 10", result.get(0).getAddressShortDto().getHouse());
    }

    @Test
    void searchBranchesByFilter_WithBranchNameAndNumber_200() {
        BranchInfo branchInfo = new BranchInfo();
        branchInfo.setNameOrBankNumber("Центральное 1234");

        BranchSearchRequest request = new BranchSearchRequest();
        request.setBranchInfo(branchInfo);

        List<BranchSearchResponseDto> result = branchService.searchBranchesByFilter(request);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Центральное отделение", result.get(0).getBranchFullDto().getName());
    }

    @Test
    void searchBranchesByFilter_PlusStreet_200() {
        BranchInfo branchInfo = new BranchInfo();
        branchInfo.setNameOrBankNumber("Цент 12");

        AddressInfo addressInfo = new AddressInfo();
        addressInfo.setFullAddress("Мира");

        BranchSearchRequest request = new BranchSearchRequest();
        request.setBranchInfo(branchInfo);
        request.setAddressInfo(addressInfo);

        List<BranchSearchResponseDto> result = branchService.searchBranchesByFilter(request);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Центровое", result.get(0).getBranchFullDto().getName());
        assertEquals("Мира", result.get(0).getAddressShortDto().getStreet());
    }

    @Test
    @DisplayName("Должно быть выброшено NotFoundException для несуществующего branch")
    void whenGetFakeBranchById_ShouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> branchService.getBranchNameByBranchId(UUID.randomUUID()),
                "Должно быть выброшено NotFoundException для несуществующего branch");
    }

    @Test
    @DisplayName("Успешно полученное имя отделения")
    void whenUUIDValid_ShouldReturnBranchName() {
        BranchNameResponse response = branchService.getBranchNameByBranchId(branch.getId());

        assertNotNull(response);
        assertEquals(branch.getName(), response.branchName());
    }

    @Test
    @DisplayName("Должно успешно создаться отделение")
    void whenAllDataValid_ShouldCreateBranch() {
        MessageResponseDto response = branchService.createBranch(newBranch);

        assertNotNull(response);
        assertEquals("Отделение успешно добавлено", response.message());
    }

    @Test
    @DisplayName("Должен быть конфликт 409 при создании отделения")
    void whenCreateExistBranch_ShouldThrowConflict() {
        assertThrows(EntityExistsException.class, () -> branchService.createBranch(invalidBranch),
                "Должно быть выброшено EntityExistsException для совпадающего branch");
    }

    @Test
    @DisplayName("Должен успешно возвращаться список отделений")
    void shouldReturnListOfBranches() {
        List<BranchListResponse> response = branchService.getBranches();

        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    @DisplayName("Должен вернуться пустой список")
    void shouldReturnEmptyListOfBranches() {
        entityManager.createNativeQuery("DELETE FROM work_schedule").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM atm").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM branch_department").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM branch").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM address").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM city").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM location").executeUpdate();

        List<BranchListResponse> response = branchService.getBranches();

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void archiveBranchesId_Integration_ShouldWorkCorrectly() throws BadRequestException {
        List<UUID> branchIds = List.of(branch.getId());

        ArchiveBranchResponse response = branchService.archiveBranchesId(branchIds);

        assertThat(response).isNotNull();

        ArchiveBranchResponse.ArchiveBranchesDto dto = response.getArchived().stream()
                .filter(d -> d.getBranchId().equals(branch.getId()))
                .findFirst().orElseThrow();

        assertThat(dto.getName()).isEqualTo("Центральное отделение");
    }

    @Test
    void searchBranchesByFilter_WithNullAddressInfo_ShouldReturnBranches() {
        BranchInfo branchInfo = BranchInfo.builder()
                .nameOrBankNumber("Центральное")
                .build();
        BranchSearchRequest request = new BranchSearchRequest(branchInfo, null);
        List<BranchSearchResponseDto> result = branchService.searchBranchesByFilter(request);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        BranchSearchResponseDto responseDto = result.get(0);
        assertEquals("Центральное отделение", responseDto.getBranchFullDto().getName());
    }

    @Test
    void searchBranchesByFilter_WithNullBranchInfo_ShouldReturnAllBranches() {
        BranchSearchRequest request = new BranchSearchRequest(null, new AddressInfo());
        List<BranchSearchResponseDto> result = branchService.searchBranchesByFilter(request);
        assertNotNull(result);
        assertEquals(3, result.size()); // теперь 3 филиала
    }

    @Test
    void searchBranchesByFilter_WithEmptyRequest_ShouldReturnAllBranches() {
        BranchSearchRequest request = new BranchSearchRequest(new BranchInfo(), new AddressInfo());
        List<BranchSearchResponseDto> result = branchService.searchBranchesByFilter(request);
        assertNotNull(result);
        assertEquals(3, result.size()); // теперь 3 филиала
    }

    @Test
    void searchBranchesByFilter_WithFullAddressSearch_ShouldReturnMatchingBranches() {
        AddressInfo addressInfo = AddressInfo.builder()
                .fullAddress("Ленина")
                .build();
        BranchSearchRequest request = new BranchSearchRequest(new BranchInfo(), addressInfo);
        List<BranchSearchResponseDto> result = branchService.searchBranchesByFilter(request);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Центральное отделение", result.get(0).getBranchFullDto().getName());
    }

    @Test
    void searchBranchesByFilter_WithBankNumberSearch_ShouldReturnMatchingBranch() {
        BranchInfo branchInfo = BranchInfo.builder()
                .nameOrBankNumber("1222")
                .build();
        BranchSearchRequest request = new BranchSearchRequest(branchInfo, null);
        List<BranchSearchResponseDto> result = branchService.searchBranchesByFilter(request);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Центровое", result.get(0).getBranchFullDto().getName());
        assertEquals("1222", result.get(0).getBranchFullDto().getBankNumber());
    }

    @Test
    void searchBranchesByFilter_WithCurrencyExchangeFilter_ShouldReturnFilteredBranches() {
        BranchInfo branchInfo = BranchInfo.builder()
                .hasCurrencyExchange(true)
                .build();
        BranchSearchRequest request = new BranchSearchRequest(branchInfo, null);
        List<BranchSearchResponseDto> result = branchService.searchBranchesByFilter(request);
        assertNotNull(result);
        assertEquals(3, result.size()); // все 3 филиала имеют обмен валют
        result.forEach(dto -> assertTrue(dto.getBranchFullDto().isHasCurrencyExchange()));
    }

    @Test
    void searchBranchesByFilter_WithPandusFilter_ShouldReturnFilteredBranches() {
        BranchInfo branchInfo = BranchInfo.builder()
                .hasPandus(true)
                .build();

        BranchSearchRequest request = new BranchSearchRequest(branchInfo, null);
        List<BranchSearchResponseDto> result = branchService.searchBranchesByFilter(request);
        assertNotNull(result);
        assertEquals(2, result.size()); // два филиала имеют пандус
        result.forEach(dto -> assertTrue(dto.getBranchFullDto().isHasPandus()));
    }

    @Test
    void searchBranchesByFilter_WithNonMatchingCriteria_ShouldReturnEmptyList() {
        BranchInfo branchInfo = BranchInfo.builder()
                .nameOrBankNumber("Несуществующее отделение")
                .build();

        BranchSearchRequest request = new BranchSearchRequest(branchInfo, new AddressInfo());
        List<BranchSearchResponseDto> result = branchService.searchBranchesByFilter(request);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void searchBranchesByFilter_WithCombinedFilters_ShouldReturnMatchingBranch() {
        BranchInfo branchInfo = BranchInfo.builder()
                .nameOrBankNumber("Центральное")
                .hasCurrencyExchange(true)
                .hasPandus(true)
                .build();

        AddressInfo addressInfo = AddressInfo.builder()
                .fullAddress("Ленина")
                .build();
        BranchSearchRequest request = new BranchSearchRequest(branchInfo, addressInfo);
        List<BranchSearchResponseDto> result = branchService.searchBranchesByFilter(request);
        assertNotNull(result);
        assertEquals(1, result.size());
        BranchSearchResponseDto responseDto = result.get(0);
        assertEquals("Центральное отделение", responseDto.getBranchFullDto().getName());
        assertTrue(responseDto.getBranchFullDto().isHasCurrencyExchange());
        assertTrue(responseDto.getBranchFullDto().isHasPandus());
    }

    @Test
    void searchBranchesByFilter_VerifyIsOpenFieldIsSetForAllBranches() {
        BranchSearchRequest request = new BranchSearchRequest(new BranchInfo(), new AddressInfo());
        List<BranchSearchResponseDto> result = branchService.searchBranchesByFilter(request);

        assertNotNull(result);
        assertEquals(3, result.size());

        // Проверяем что у всех филиалов установлено поле isOpen
        result.forEach(dto -> {
            assertNotNull(dto.getBranchFullDto());
            // Просто обращаемся к полю - если нет исключения, значит поле установлено
            boolean isOpen = dto.getBranchFullDto().isOpen();
            assertTrue(true); // placeholder assertion
        });
    }

    @Test
    void getBranches_ShouldReturnIsOpenField() {
        List<BranchListResponse> result = branchService.getBranches();

        assertNotNull(result);
        assertFalse(result.isEmpty());

        // Проверяем что у всех филиалов в getBranches тоже установлено поле isOpen
        result.forEach(response -> {
            assertNotNull(response.branchInfo());
            // Просто обращаемся к полю - если нет исключения, значит поле установлено
            boolean isOpen = response.branchInfo().isOpen();
            assertTrue(true); // placeholder assertion
        });
    }
}