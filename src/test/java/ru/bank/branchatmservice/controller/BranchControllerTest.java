package ru.bank.branchatmservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.bank.branchatmservice.dto.AddressShortDto;
import ru.bank.branchatmservice.dto.BranchCreateDto;
import ru.bank.branchatmservice.dto.BranchCreateInfoDto;
import ru.bank.branchatmservice.dto.BranchDto;
import ru.bank.branchatmservice.dto.BranchFullDto;
import ru.bank.branchatmservice.dto.ScheduleDto;
import ru.bank.branchatmservice.dto.request.AddressFullInfoDto;
import ru.bank.branchatmservice.dto.BranchIdListDto;
import ru.bank.branchatmservice.dto.request.AddressInfo;
import ru.bank.branchatmservice.dto.request.BranchAndAddressInfoRequest;
import ru.bank.branchatmservice.dto.request.BranchInfo;
import ru.bank.branchatmservice.dto.request.BranchSearchRequest;
import ru.bank.branchatmservice.dto.request.BranchUpdateRequestDto;
import ru.bank.branchatmservice.dto.response.ArchiveBranchResponse;
import ru.bank.branchatmservice.dto.response.BranchAndAddressInfoResponse;
import ru.bank.branchatmservice.dto.response.BranchBankNumberDTO;
import ru.bank.branchatmservice.dto.response.BranchDtoView;
import ru.bank.branchatmservice.dto.response.BranchListResponse;
import ru.bank.branchatmservice.dto.response.BranchSearchResponseDto;
import ru.bank.branchatmservice.dto.response.BranchUnionDto;
import ru.bank.branchatmservice.dto.response.MessageResponseDto;
import ru.bank.branchatmservice.enums.BranchType;
import ru.bank.branchatmservice.exception.BranchNotFoundException;
import ru.bank.branchatmservice.exception.NotFoundException;
import ru.bank.branchatmservice.service.BranchService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BranchController.class)
public class BranchControllerTest {
    BranchFullDto branchFullDto;
    AddressShortDto addressShortDto;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BranchService branchService;

    private final UUID BRANCH_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private final String BRANCH_ID_STRING = "123e4567-e89b-12d3-a456-426614174000";


    @BeforeEach
    void setup() {
        branchFullDto = BranchFullDto.builder()
                .id(UUID.randomUUID())
                .name("BranchName")
                .bankNumber("1111")
                .addressId(UUID.randomUUID())
                .phoneNumber("+77777777777")
                .hasCurrencyExchange(true)
                .hasPandus(true)
                .isClosed(false)
                .build();

        addressShortDto = AddressShortDto.builder()
                .cityName("CityNameTest")
                .streetType("ул.")
                .street("Тестовая")
                .house("10")
                .build();
    }

    @Test
    void searchBranchByFilterTest_200() throws Exception {
        BranchInfo branchInfo = BranchInfo.builder()
                .nameOrBankNumber("BranchName 1111")
                .hasCurrencyExchange(true)
                .hasPandus(true)
                .isClosed(false)
                .build();
        AddressInfo addressInfo = AddressInfo.builder()
                .fullAddress("CityNameTest ул. Тестовая")
                .build();
        BranchSearchRequest branchSearchRequest = new BranchSearchRequest(branchInfo, addressInfo);
        BranchSearchResponseDto branchSearchResponseDto = new BranchSearchResponseDto(branchFullDto, addressShortDto);
        List<BranchSearchResponseDto> responseList = List.of(branchSearchResponseDto);

        when(branchService.searchBranchesByFilter(any(BranchSearchRequest.class))).thenReturn(responseList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/branches/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(branchSearchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].branchFullDto.name", containsString("BranchName")));

        verify(branchService, times(1)).searchBranchesByFilter(any(BranchSearchRequest.class));
    }

    @Test
    void searchBranchByFilterTest_404() throws Exception {
        BranchInfo branchInfo = BranchInfo.builder()
                .nameOrBankNumber("NonExistentBranch 9999")
                .build();

        BranchSearchRequest branchSearchRequest = new BranchSearchRequest(branchInfo, null);

        when(branchService.searchBranchesByFilter(any(BranchSearchRequest.class)))
                .thenThrow(new NotFoundException("Нет отделений по таким критериям поиска"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/branches/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(branchSearchRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void findBranchInfoByBankNumber_WhenExistingParam_ShouldReturnSuccess() throws Exception {
        String bankNumber = "1234567890";

        BranchAndAddressInfoResponse mockResponse = new BranchAndAddressInfoResponse(branchFullDto, addressShortDto);

        when(branchService.getBranchAndAddressInfo(new BranchAndAddressInfoRequest(bankNumber)))
                .thenReturn(mockResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/branches")
                        .param("bank_number", bankNumber).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(branchFullDto)))
                .andExpect(status().isOk());
    }

    @Test
    void findBranchInfoByBankNumber_WhenWrongParam_ShouldReturnNotFound() throws Exception {
        when(branchService.getBranchAndAddressInfo(any(BranchAndAddressInfoRequest.class)))
                .thenThrow(new NotFoundException("Данные не найдены"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/branches/search/{number}", "9999")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    void getBranchBankNumberListByBankNumber_ShouldReturnListOfBankNumbers() throws Exception {
        List<BranchBankNumberDTO> mockResponse = Arrays.asList(
                new BranchBankNumberDTO("101"),
                new BranchBankNumberDTO("102"),
                new BranchBankNumberDTO("103")
        );

        when(branchService.getBranchBankNumberByBankNumber("10")).thenReturn(mockResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/branches/similar-numbers")
                        .param("bankNumber", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].bankNumber").value("101"))
                .andExpect(jsonPath("$[1].bankNumber").value("102"))
                .andExpect(jsonPath("$[2].bankNumber").value("103"));
    }

    @Test
    void getBranchBankNumberListByBankNumber_ShouldReturnEmptyList() throws Exception {
        when(branchService.getBranchBankNumberByBankNumber("99"))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/branches/similar-numbers")
                        .param("bankNumber", "99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getBranchBankNumberListByBankNumber_ShouldReturn400WhenParameterMissing() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/branches/similar-numbers"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBranchBankNumberListByBankNumber_ShouldHandleNotFoundException() throws Exception {
        when(branchService.getBranchBankNumberByBankNumber("99"))
                .thenThrow(new NotFoundException("Данные не найдены"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/branches/similar-numbers")
                        .param("bankNumber", "99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBranch_WhenBranchExists_ShouldReturnBranchUnionDto() throws Exception {
        // Given
        BranchUnionDto branchUnionDto = createTestBranchUnionDto();

        when(branchService.findBranchById(BRANCH_ID)).thenReturn(branchUnionDto);

        // When & Then
        mockMvc.perform(get("/api/v1/branches/{branch_id}", BRANCH_ID_STRING)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.branchInfo.name").value("ДО «ГУМ»"))
                .andExpect(jsonPath("$.branchInfo.bankNumber").value("101"))
                .andExpect(jsonPath("$.branchInfo.phoneNumber").value("78478579955"))
                .andExpect(jsonPath("$.branchInfo.hasCurrencyExchange").value(true))
                .andExpect(jsonPath("$.branchInfo.hasPandus").value(true))
                .andExpect(jsonPath("$.branchInfo.isClosed").value(true))
                .andExpect(jsonPath("$.workSchedule").isArray());
    }

    @Test
    void getBranch_WhenBranchNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        when(branchService.findBranchById(BRANCH_ID))
                .thenThrow(new BranchNotFoundException(String.format("Отделение с id %s не найдено", BRANCH_ID)));

        // When & Then
        mockMvc.perform(get("/api/v1/branches/{branch_id}", BRANCH_ID_STRING)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(String.format("Отделение с id %s не найдено", BRANCH_ID)));
    }

    @Test
    void getBranch_WithInvalidUUID_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/branches/{branch_id}", "invalid-uuid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    void whenUpdateBranchWithValidData_thenReturnsUpdatedBranch() throws Exception {
        // Given
        String updateRequest = createTestJsonBranchUpdateRequestDto();
        BranchUpdateRequestDto branchUpdateRequestDto = objectMapper.readValue(updateRequest, BranchUpdateRequestDto.class);

        // When & Then
        mockMvc.perform(patch("/api/v1/branches/{branchId}", BRANCH_ID_STRING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successfulMessage")
                        .value("Отделение успешно отредактировано"));
    }


    @Test
    void patchBranch_WithInvalidUUID_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(patch("/api/v1/branches/{branch_id}", "invalid-uuid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    private BranchUnionDto createTestBranchUnionDto() {
        BranchDtoView branchDtoView = new BranchDtoView();
        branchDtoView.setName("ДО «ГУМ»");
        branchDtoView.setBankNumber("101");
        branchDtoView.setPhoneNumber("78478579955");
        branchDtoView.setHasCurrencyExchange(true);
        branchDtoView.setHasPandus(true);
        branchDtoView.setIsClosed(true);

        ScheduleDto scheduleDto = new ScheduleDto(1, "08:00", "19:00");

        return new BranchUnionDto(branchDtoView, List.of(scheduleDto));
    }

    private String createTestJsonBranchUpdateRequestDto() {

        return """
                {
                   "branchInfo": {
                         "name": "ДО «ГУМ»",
                         "bankNumber": "101",
                         "hasCurrencyExchange": "True",
                         "phoneNumber": "+78478579955",
                         "hasPandus": "True",
                         "type": "Филиал"
                     },
                   "addressInfo": {
                         "cityId": "73306416-a806-11f0-a6a1-332275b2a453",
                         "streetType": "ул.",
                         "street": "Ленина",
                         "house":"5 к.1",
                         "latitude":"34.456789",
                         "longitude":"14.345678",
                         "metroStation":"Белорусская"
                     },
                   "workSchedule": [
                     {
                         "openingTime":"08:00",
                         "closingTime":"22:00",
                         "weekDay": 1,
                         "optional":"add"
                      },
                      {
                         "openingTime":"08:30",
                         "weekDay": 2,
                         "optional":"CHANGE"
                      },
                      {
                         "weekDay": 3,
                         "optional":"DELETE"
                      }
                   ]
                 }
            """;
    }

    @Test
    @DisplayName("Должен вернуться ответ об успешном создании отделения")
    void shouldReturnResponseMessage_ifIdValid() throws Exception {
        MessageResponseDto message = new MessageResponseDto(
                "Отделение успешно добавлено",
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
        BranchCreateDto newBranch = new BranchCreateDto(
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

        when(branchService.createBranch(any(BranchCreateDto.class)))
                .thenReturn(message);

        String result = mockMvc.perform(post("/api/v1/branches")
                        .content(objectMapper.writeValueAsString(newBranch))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertEquals(objectMapper.writeValueAsString(message), result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            // 1. Отсутствует branchInfo
            """
            {
                "addressInfo": {
                    "cityName": "Москва",
                    "streetType": "ул.",
                    "street": "Ленина",
                    "house": "1",
                    "latitude": "55.754167",
                    "longitude": "37.620000",
                    "metroStation": "Охотный Ряд"
                },
                "scheduleArray": [
                    {
                        "weekDay": 1,
                        "openingTime": "09:00",
                        "closingTime": "20:00"
                    }
                ]
            }
            """,

            // 2. Отсутствует addressInfo
            """
            {
                "branchInfo": {
                    "name": "ДО «ГУМ»",
                    "bankNumber": "101",
                    "hasCurrencyExchange": true,
                    "hasPandus": true,
                    "isClosed": false,
                    "phoneNumber": "+79991234567",
                    "type": "BRANCH"
                },
                "scheduleArray": [
                    {
                        "weekDay": 1,
                        "openingTime": "09:00",
                        "closingTime": "20:00"
                    }
                ]
            }
            """,

            // 3. Отсутствует scheduleArray
            """
            {
                "branchInfo": {
                    "name": "ДО «ГУМ»",
                    "bankNumber": "101",
                    "hasCurrencyExchange": true,
                    "hasPandus": true,
                    "isClosed": false,
                    "phoneNumber": "+79991234567",
                    "type": "BRANCH"
                },
                "addressInfo": {
                    "cityName": "Москва",
                    "streetType": "ул.",
                    "street": "Ленина",
                    "house": "1",
                    "latitude": "55.754167",
                    "longitude": "37.620000",
                    "metroStation": "Охотный Ряд"
                }
            }
            """,

            // 4. Пустой branchInfo
            """
            {
                "branchInfo": {},
                "addressInfo": {
                    "cityName": "Москва",
                    "streetType": "ул.",
                    "street": "Ленина",
                    "house": "1",
                    "latitude": "55.754167",
                    "longitude": "37.620000",
                    "metroStation": "Охотный Ряд"
                },
                "scheduleArray": [
                    {
                        "weekDay": 1,
                        "openingTime": "09:00",
                        "closingTime": "20:00"
                    }
                ]
            }
            """,

            // 5. Невалидный телефон (не соответствует формату)
            """
            {
                "branchInfo": {
                    "name": "ДО «ГУМ»",
                    "bankNumber": "101",
                    "hasCurrencyExchange": true,
                    "hasPandus": true,
                    "isClosed": false,
                    "phoneNumber": "89991234567",
                    "type": "BRANCH"
                },
                "addressInfo": {
                    "cityName": "Москва",
                    "streetType": "ул.",
                    "street": "Ленина",
                    "house": "1",
                    "latitude": "55.754167",
                    "longitude": "37.620000",
                    "metroStation": "Охотный Ряд"
                },
                "scheduleArray": [
                    {
                        "weekDay": 1,
                        "openingTime": "09:00",
                        "closingTime": "20:00"
                    }
                ]
            }
            """,

            // 6. Имя превышает 64 символа
            """
            {
                "branchInfo": {
                    "name": "Очень длинное название отделения которое точно превышает лимит в шестьдесят четыре символа",
                    "bankNumber": "101",
                    "hasCurrencyExchange": true,
                    "hasPandus": true,
                    "isClosed": false,
                    "phoneNumber": "+79991234567",
                    "type": "BRANCH"
                },
                "addressInfo": {
                    "cityName": "Москва",
                    "streetType": "ул.",
                    "street": "Ленина",
                    "house": "1",
                    "latitude": "55.754167",
                    "longitude": "37.620000",
                    "metroStation": "Охотный Ряд"
                },
                "scheduleArray": [
                    {
                        "weekDay": 1,
                        "openingTime": "09:00",
                        "closingTime": "20:00"
                    }
                ]
            }
            """,

            // 7. Невалидное время (24:00)
            """
            {
                "branchInfo": {
                    "name": "ДО «ГУМ»",
                    "bankNumber": "101",
                    "hasCurrencyExchange": true,
                    "hasPandus": true,
                    "isClosed": false,
                    "phoneNumber": "+79991234567",
                    "type": "BRANCH"
                },
                "addressInfo": {
                    "cityName": "Москва",
                    "streetType": "ул.",
                    "street": "Ленина",
                    "house": "1",
                    "latitude": "55.754167",
                    "longitude": "37.620000",
                    "metroStation": "Охотный Ряд"
                },
                "scheduleArray": [
                    {
                        "weekDay": 1,
                        "openingTime": "24:00",
                        "closingTime": "20:00"
                    }
                ]
            }
            """,

            // 8. День недели вне диапазона (8)
            """
            {
                "branchInfo": {
                    "name": "ДО «ГУМ»",
                    "bankNumber": "101",
                    "hasCurrencyExchange": true,
                    "hasPandus": true,
                    "isClosed": false,
                    "phoneNumber": "+79991234567",
                    "type": "BRANCH"
                },
                "addressInfo": {
                    "cityName": "Москва",
                    "streetType": "ул.",
                    "street": "Ленина",
                    "house": "1",
                    "latitude": "55.754167",
                    "longitude": "37.620000",
                    "metroStation": "Охотный Ряд"
                },
                "scheduleArray": [
                    {
                        "weekDay": 8,
                        "openingTime": "09:00",
                        "closingTime": "20:00"
                    }
                ]
            }
            """,

            // 9. Невалидная широта (100.0)
            """
            {
                "branchInfo": {
                    "name": "ДО «ГУМ»",
                    "bankNumber": "101",
                    "hasCurrencyExchange": true,
                    "hasPandus": true,
                    "isClosed": false,
                    "phoneNumber": "+79991234567",
                    "type": "BRANCH"
                },
                "addressInfo": {
                    "cityName": "Москва",
                    "streetType": "ул.",
                    "street": "Ленина",
                    "house": "1",
                    "latitude": "100.0",
                    "longitude": "37.620000",
                    "metroStation": "Охотный Ряд"
                },
                "scheduleArray": [
                    {
                        "weekDay": 1,
                        "openingTime": "09:00",
                        "closingTime": "20:00"
                    }
                ]
            }
            """,

            // 10. Пустой массив scheduleArray
            """
            {
                "branchInfo": {
                    "name": "ДО «ГУМ»",
                    "bankNumber": "101",
                    "hasCurrencyExchange": true,
                    "hasPandus": true,
                    "isClosed": false,
                    "phoneNumber": "+79991234567",
                    "type": "BRANCH"
                },
                "addressInfo": {
                    "cityName": "Москва",
                    "streetType": "ул.",
                    "street": "Ленина",
                    "house": "1",
                    "latitude": "55.754167",
                    "longitude": "37.620000",
                    "metroStation": "Охотный Ряд"
                },
                "scheduleArray": []
            }
            """,

            // 10. Пустой массив scheduleArray
            """
            {
                "branchInfo": {
                    "name": "ДО «ГУМ»",
                    "bankNumber": "101",
                    "hasCurrencyExchange": true,
                    "hasPandus": true,
                    "isClosed": false,
                    "phoneNumber": "+79991234567",
                    "type": "BRANCH"
                },
                "addressInfo": {
                    "cityName": "Москва",
                    "streetType": "ул.",
                    "street": "Ленина",
                    "house": "1",
                    "latitude": "55.754167",
                    "longitude": "37.620000",
                    "metroStation": "Охотный Ряд"
                },
                "scheduleArray": []
            }
            """}
    )
    @DisplayName("Невалидные значения 400 - Bad Request")
    void shouldReturnBadRequest_whenDataInvalid(String invalidNewBranch) throws Exception {
        mockMvc.perform(post("/api/v1/branches")
                .content(invalidNewBranch)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Должен вернуться список отделений")
    void shouldReturnListOfBranches() throws Exception {
        BranchDto infoDto = new BranchDto(
                UUID.randomUUID(),
                "Центральный офис",
                "123456789",
                true,
                false,
                false,
                "+78478579955",
                true
        );
        AddressShortDto addressDto = new AddressShortDto(
                "Москва",
                "ул.",
                "Тверская",
                "15"
        );
        List<BranchListResponse> response = List.of(new BranchListResponse(infoDto, addressDto));

        when(branchService.getBranches())
                .thenReturn(response);

        String result = mockMvc.perform(get("/api/v1/branches")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertEquals(objectMapper.writeValueAsString(response), result);
    }

    @Test
    void searchBranchByFilterReturnEmptyListIfNothingFound() {
        //given
        BranchSearchRequest bsr = new BranchSearchRequest(new BranchInfo(), new AddressInfo());
        List<BranchSearchResponseDto> searchResponseList = Collections.emptyList();

        when(branchService.searchBranchesByFilter(bsr)).thenReturn(searchResponseList);

        //when
        List<BranchSearchResponseDto> result = branchService.searchBranchesByFilter(bsr);

        //then
        Assertions.assertDoesNotThrow(() -> branchService.searchBranchesByFilter(bsr));
        Assertions.assertTrue(result.isEmpty());

        verify(branchService, times(2)).searchBranchesByFilter(bsr);
    }

    @Test
    void archiveBranch_WhenRightParam_ShouldReturnSuccess() throws Exception {

        UUID branchId1 = UUID.fromString("42e0623c-6e6d-11ed-a1eb-0242ac120004");
        UUID branchId2 = UUID.fromString("52f1734d-7f7e-11ed-a1eb-0242ac120004");

       ArchiveBranchResponse.ArchiveBranchesDto response1 = new ArchiveBranchResponse.ArchiveBranchesDto();
        response1.setBranchId(branchId1);
        response1.setName("ДО «ГУМ»");

        ArchiveBranchResponse.ArchiveBranchesDto response2 = new ArchiveBranchResponse.ArchiveBranchesDto();
        response2.setBranchId(branchId2);
        response2.setName("ДО «Центральный»");

        BranchIdListDto requestIds = new BranchIdListDto(List.of(branchId1,branchId2));
        ArchiveBranchResponse expectedResponse = new ArchiveBranchResponse();
        expectedResponse.setArchived(List.of(response1, response2));

        when(branchService.archiveBranchesId(anyList())).thenReturn(expectedResponse);

        mockMvc.perform(put("/api/v1/branches/archive")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.archived.length()").value(2))
                .andExpect(jsonPath("$.archived[0].branchId").value(branchId1.toString()))
                .andExpect(jsonPath("$.archived[0].name").value("ДО «ГУМ»"))
                .andExpect(jsonPath("$.archived[1].branchId").value(branchId2.toString()))
                .andExpect(jsonPath("$.archived[1].name").value("ДО «Центральный»"));
    }
}