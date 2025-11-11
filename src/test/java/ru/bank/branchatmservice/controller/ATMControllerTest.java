package ru.bank.branchatmservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.util.MultiValueMap;
import ru.bank.branchatmservice.dto.ATMFullDto;
import ru.bank.branchatmservice.dto.AddressShortDto;
import ru.bank.branchatmservice.dto.AtmInfoDto;
import ru.bank.branchatmservice.dto.BranchInfoDto;
import ru.bank.branchatmservice.dto.CoordinatesDto;
import ru.bank.branchatmservice.dto.InfoAtmDto;
import ru.bank.branchatmservice.dto.ScheduleDto;
import ru.bank.branchatmservice.dto.request.AtmCreateDto;
import ru.bank.branchatmservice.dto.request.AtmFilterDto;
import ru.bank.branchatmservice.dto.request.UpdateAtmInfoRequest;
import ru.bank.branchatmservice.dto.response.AtmFilterResponseDto;
import ru.bank.branchatmservice.dto.response.MessageResponseDto;
import ru.bank.branchatmservice.exception.NotFoundException;
import ru.bank.branchatmservice.service.ATMService;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ATMController.class)
public class ATMControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ATMService atmService;

    @Test
    @DisplayName("Должна вернуться полная информация по банкомату")
    void shouldReturnATMFullDto_ifIdValid() throws Exception {
        ATMFullDto atmFullDto = new ATMFullDto(
                new AtmInfoDto(
                        true,
                        new AddressShortDto("Москва", "ул.", "Тверская", "15"),
                        new CoordinatesDto(new BigDecimal("55.671661"), new BigDecimal("37.640670")),
                        "Тверская",
                        "11",
                        "Второй этаж, левое крыло",
                        "Внешний",
                        true,
                        true
                ),
                new BranchInfoDto(
                        "ДО «ГУМ»",
                        "101",
                        "+74951112233",
                        new AddressShortDto("Москва", "ул.", "Тверская", "15")
                ),
                List.of(
                        new ScheduleDto(1, "08:00", "19:00"),
                        new ScheduleDto(2, "08:00", "19:00"),
                        new ScheduleDto(3, "08:00", "19:00"),
                        new ScheduleDto(4, "08:00", "19:00"),
                        new ScheduleDto(5, "08:00", "19:00"),
                        new ScheduleDto(6, "09:00", "17:00"),
                        new ScheduleDto(7, "10:00", "16:00")
                )
        );

        when(atmService.getATMById(any(UUID.class))).thenReturn(atmFullDto);

        String result = mockMvc.perform(get("/api/v1/atms/{atmId}", UUID.randomUUID())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertEquals(objectMapper.writeValueAsString(atmFullDto), result);
    }

    @Test
    void softDeleteATMByIds_ShouldReturnSuccessResponse_200() throws Exception {
        UUID uuid1 = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID uuid2 = UUID.fromString("6ba7b810-9dad-11d1-80b4-00c04fd430c8");
        List<UUID> atmIds = List.of(uuid1, uuid2);

        InfoAtmDto infoAtm1 = new InfoAtmDto("ATM-001", "Москва", "ул.", "Ленина", "10");
        InfoAtmDto infoAtm2 = new InfoAtmDto("ATM-002", "Санкт-Петербург", "пр.", "Невский", "25");
        List<InfoAtmDto> infoAtmDtos = List.of(infoAtm1, infoAtm2);


        when(atmService.softDeleteATMByIds(atmIds)).thenReturn(infoAtmDtos);

        mockMvc.perform(put("/api/v1/atms/archive")
                        .param("atmIds", uuid1.toString(), uuid2.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].inventoryNumber").value("ATM-001"))
                .andExpect(jsonPath("$[1].inventoryNumber").value("ATM-002"));

        verify(atmService, times(1)).softDeleteATMByIds(atmIds);
    }

    @Test
    void softDeleteATMByIds_ShouldReturnBadRequest_WhenNoParameters() throws Exception {
        mockMvc.perform(put("/api/v1/atms/archive")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(atmService, never()).softDeleteATMByIds(anyList());
    }

    @Test
    void createATM_Success() throws Exception {
        // Подготовка данных
        MessageResponseDto response = new MessageResponseDto(
                "Банкомат успешно добавлен",
                "2023-10-10T12:00:00"
        );

        String requestJson = """
                {
                    "atmInfo": {
                        "ATMNumber": "1111",
                        "inventoryNumber": "0001244890",
                        "installationLocation": "Второй этаж",
                        "construction": "Внешний",
                        "cashDeposit": true,
                        "NFC": true
                    },
                    "addressInfo": {
                        "cityName": "Москва",
                        "streetType": "ал.",
                        "street": "Ленина",
                        "house": "143",
                        "latitude": "34.456789",
                        "longitude": "14.345678",
                        "metroStation": "Белорусская"
                    },
                    "branchInfo": {
                        "bankNumber": "101"
                    },
                    "scheduleArray": [
                        {
                            "weekDay": 1,
                            "openingTime": "08:00",
                            "closingTime": "19:00"
                        }
                    ]
                }
                """;

        // Настройка моков
        when(atmService.createATM(any(AtmCreateDto.class))).thenReturn(response);

        // Вызов и проверка
        mockMvc.perform(post("/api/v1/atms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Банкомат успешно добавлен"));
    }

    @Test
    void createATM_InvalidData() throws Exception {
        // Неполные данные (отсутствует обязательное поле)
        String invalidRequestJson = """
                {
                    "atmInfo": {
                        "ATMNumber": "1111"
                        // отсутствуют другие обязательные поля
                    },
                    "addressInfo": {
                        "cityName": "Москва"
                    },
                    "branchInfo": {
                        "bankNumber": "101"
                    },
                    "scheduleArray": []
                }
                """;

        mockMvc.perform(post("/api/v1/atms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void softDeleteATMByIds_BadRequest_WhenInvalidUUID() throws Exception {
        mockMvc.perform(put("/api/v1/atms/archive")
                        .param("atmIds", "invalid-uuid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(atmService, never()).softDeleteATMByIds(anyList());
    }

    @Test
    void softDeleteATMByIds_ShouldReturnNotFound_WhenServiceThrowsNotFoundException() throws Exception {
        UUID uuid1 = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        List<UUID> atmIds = List.of(uuid1);

        when(atmService.softDeleteATMByIds(atmIds))
                .thenThrow(new NotFoundException("Запрашиваемы данные не найдены"));

        mockMvc.perform(put("/api/v1/atms/archive")
                        .param("atmIds", uuid1.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Запрашиваемы данные не найдены"));

        verify(atmService, times(1)).softDeleteATMByIds(atmIds);
    }

    @Test
    void softDeleteATMByIds_ShouldHandleSingleATM_200() throws Exception {
        UUID uuid1 = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        List<UUID> atmIds = List.of(uuid1);

        InfoAtmDto infoAtm1 = new InfoAtmDto("ATM-001", "Москва", "ул.", "Ленина", "10");
        List<InfoAtmDto> infoAtmDtos = List.of(infoAtm1);

        when(atmService.softDeleteATMByIds(atmIds)).thenReturn(infoAtmDtos);

        mockMvc.perform(put("/api/v1/atms/archive")
                        .param("atmIds", uuid1.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].inventoryNumber").value("ATM-001"));

        verify(atmService, times(1)).softDeleteATMByIds(atmIds);
    }

    @Test
    void updateATMById_ShouldReturnNoContent_WhenUpdateSuccessful() throws Exception {
        // Given
        UUID atmId = UUID.randomUUID();
        String requestBody = """
                {
                    "atmInfo": {
                         "inventoryNumber": "000124489",
                         "installationLocation": "Второй этаж, правое крыло",
                         "construction": "внутренний",
                         "cashDeposit": false,
                         "nfc": true
                       },
                       "addressInfo": {
                         "cityName": "Московия",
                         "streetType": "ал.",
                         "street": "Ленину",
                         "house": "144",
                         "latitude": "33.456789",
                         "longitude": "13.345678",
                         "metroStation": "Русская"
                       },
                       "schedules": [
                         {
                           "weekDay": 1,
                           "openingTime": "08:00",
                           "closingTime": "19:00"
                         }
                       ],
                       "branchInfo": {
                         "bankNumber": "102"
                       }
                }
                """;

        doNothing().when(atmService).updateATM(eq(atmId), any(UpdateAtmInfoRequest.class));

        // When & Then
        mockMvc.perform(put("/api/v1/atms/{atmId}", atmId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNoContent());
    }

    @Test
    void softDeleteATMByIdsReturnEmptyListIfNothingFound() {
        //given
        List<UUID> uuidList = List.of(UUID.randomUUID(), UUID.randomUUID());
        List<InfoAtmDto> infoAtmList = Collections.emptyList();

        when(atmService.softDeleteATMByIds(uuidList)).thenReturn(infoAtmList);

        //when
        List<InfoAtmDto> result = atmService.softDeleteATMByIds(uuidList);

        //then
        assertDoesNotThrow(() -> atmService.softDeleteATMByIds(uuidList));
        assertTrue(result.isEmpty());

        verify(atmService, times(2)).softDeleteATMByIds(uuidList);
    }

    @Test
    @DisplayName("Получение непустого списка банкоматов по фильтру")
    void getAtmsByFilter_NotEmpty() throws Exception{
        MultiValueMap<String, String> filters = new HttpHeaders();
        filters.setAll(Map.of(
                "inventoryNumber", "0012",
                "hour24", "false",
                "workingNow", "true",
                "cashDeposit", "true",
                "nfc", "false",
                "city", "Москва"));

        AtmFilterResponseDto atmDto = new AtmFilterResponseDto(
                new AtmFilterResponseDto.AtmInfo(
                        "0001244892",
                        "Внутренний",
                        true,
                        false
                ),
                new AddressShortDto("Москва", "ул.", "Тверская", "15"),
                List.of(
                        new ScheduleDto(1, "08:00", "19:00"),
                        new ScheduleDto(2, "08:00", "19:00"),
                        new ScheduleDto(3, "08:00", "19:00"),
                        new ScheduleDto(4, "08:00", "19:00"),
                        new ScheduleDto(5, "08:00", "19:00"),
                        new ScheduleDto(6, "09:00", "17:00"),
                        new ScheduleDto(7, "10:00", "16:00")
                )
        );

        when(atmService.getAtmListByFilter(any(AtmFilterDto.class))).thenReturn(Collections.singletonList(atmDto));

        mockMvc.perform(get("/api/v1/atms/filter")
                        .queryParams(filters)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].atmInfo.inventoryNumber").value("0001244892"));

        verify(atmService, times(1)).getAtmListByFilter(any(AtmFilterDto.class));
    }

    @Test
    @DisplayName("Получение пустого списка банкоматов по фильтру")
    void getAtmsByFilter_Empty() throws Exception{
        MultiValueMap<String, String> filters = new HttpHeaders();
        filters.setAll(Map.of(
                "inventoryNumber", "0012",
                "hour24", "false",
                "workingNow", "true",
                "cashDeposit", "true",
                "nfc", "true",
                "city", "Москва"));

        when(atmService.getAtmListByFilter(any(AtmFilterDto.class))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/atms/filter")
                        .queryParams(filters)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(atmService, times(1)).getAtmListByFilter(any(AtmFilterDto.class));

    }
}