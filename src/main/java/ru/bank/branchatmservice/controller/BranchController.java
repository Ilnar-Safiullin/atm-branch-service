package ru.bank.branchatmservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.bank.branchatmservice.dto.BranchIdListDto;
import ru.bank.branchatmservice.dto.request.BranchSearchRequest;
import ru.bank.branchatmservice.dto.response.ArchiveBranchResponse;
import ru.bank.branchatmservice.dto.response.BranchSearchResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.bank.branchatmservice.dto.BranchCreateDto;
import ru.bank.branchatmservice.dto.request.BranchAndAddressInfoRequest;
import ru.bank.branchatmservice.dto.request.BranchUpdateRequestDto;
import ru.bank.branchatmservice.dto.response.BranchAndAddressInfoResponse;
import ru.bank.branchatmservice.dto.response.BranchBankNumberDTO;
import ru.bank.branchatmservice.dto.response.BranchListResponse;
import ru.bank.branchatmservice.dto.response.BranchUnionDto;
import ru.bank.branchatmservice.dto.response.MessageDto;
import ru.bank.branchatmservice.dto.response.MessageResponseDto;
import ru.bank.branchatmservice.handler.ErrorResponseDto;
import ru.bank.branchatmservice.service.BranchService;

import java.util.List;
import java.util.UUID;

/**
 * Контроллер для управления отделениями банка.
 * Предоставляет API для работы с банковскими отделениями и связанными операциями.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/branches")
public class BranchController {
    private final BranchService branchService;

    @Operation(
            summary = "Поиск отделений по фильтру",
            description = """
                    Возвращает список отделений банка, соответствующих заданным критериям поиска.
                    Поддерживает поиск по характеристикам отделения и адресу.
                    
                    ### Особенности поиска:
                    - По названию отделения (обязательное поле)
                    - По номеру отделения (обязательное поле)
                    - По наличию услуг: обмен валюты, пандус
                    - По статусу работы (открыто/закрыто)
                    - По адресу: город, тип улицы, улица, дом
                    - Комбинированные фильтры
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешный поиск отделений",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = BranchSearchResponseDto.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Невалидные параметры запроса",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(example = """
                                            {
                                              "timestamp": "2024-01-15T10:30:00Z",
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "Имя отделения обязательно",
                                              "path": "/api/v1/branches/filter"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Отделения не найдены",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(example = """
                                            {
                                              "timestamp": "2024-01-15T10:30:00Z",
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "Нет отделений по таким критериям поиска",
                                              "path": "/api/v1/branches/filter"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера"
                    )
            }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Параметры фильтрации отделений",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = BranchSearchRequest.class),
                    examples = @ExampleObject(
                            name = "exampleRequest",
                            summary = "Пример запроса поиска",
                            value = """
                                    {
                                      "branchInfo": {
                                        .nameOrBankNumber("BranchName 1111"),
                                        "hasCurrencyExchange": true,
                                        "hasPandus": true,
                                        "isClosed": false
                                      },
                                      "addressInfo": {
                                        .fullAddress("CityNameTest ул. Тестовая")
                                      }
                                    }
                                    """
                    )
            )
    )
    @GetMapping("/filter")
    public List<BranchSearchResponseDto> searchBranchByFilter(@Valid @RequestBody BranchSearchRequest searchRequest) {
        return branchService.searchBranchesByFilter(searchRequest);
    }

    @Operation(
            summary = "Находит отделение банка по номеру банка",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешная обработка запроса",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BranchAndAddressInfoResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Ресурс не найден",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    )
            }
    )
    @GetMapping("/search/{number}")
    public BranchAndAddressInfoResponse findBranchInfoByBankNumber(
            @PathVariable("number") String bankNumber) {
        return branchService
                .getBranchAndAddressInfo(new BranchAndAddressInfoRequest(bankNumber));
    }


    @Operation(
            summary = "Находит отделение банка по id отделения",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешная обработка запроса",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BranchUnionDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Ресурс не найден",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    )
            }
    )
    @GetMapping("/{branchId}")
    public ResponseEntity<BranchUnionDto> getBranch(@PathVariable UUID branchId) {
        return ResponseEntity.ok(branchService.findBranchById(branchId));
    }


    @Operation(
            summary = "Редактирует информацию об отделение банка по id отделения",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешная обработка запроса",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MessageDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Ресурс не найден",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    )
            }
    )
    @PatchMapping("/{branchId}")
    public ResponseEntity<MessageDto> updateBranch(@PathVariable UUID branchId,
                                                   @Valid @RequestBody BranchUpdateRequestDto branchUpdateRequestDto){

        branchService.updateBranch(branchId, branchUpdateRequestDto);

        return ResponseEntity.ok(new MessageDto("Отделение успешно отредактировано"));
    }

    @Operation(
            summary = "Получение списка отделений",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешное получение информации",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = BranchListResponse.class)))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Пользователь не авторизованный."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера. Подождите несколько минут и попробуйте снова."
                    ),
                    @ApiResponse(
                            responseCode = "503",
                            description = "Сервис временно недоступен. Повторите попытку позже."
                    ),
            }
    )
    @GetMapping()
    public List<BranchListResponse> getBranches() {
        return branchService.getBranches();
    }

    @Operation(
            summary = "Добавление отделения"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Отделение успешно добавлено",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Ошибка авторизации."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Город не найден",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(example = """
                                    {
                                      "timestamp": "2025-01-15T10:30:00Z",
                                      "status": 404 NOT FOUND,
                                      "message": "Город с наименованием Питер не найден",
                                      "path": "/api/v1/branches"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Отделение уже существует",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(example = """
                                    {
                                      "timestamp": "2025-01-15T10:30:00Z",
                                      "status": 409 CONFLICT,
                                      "message": "Отделение с таким номером телефона +79999999999 уже существует; Отделение с таким именем ДО «ГУМ» уже существует",
                                      "path": "/api/v1/branches"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Сервис временно недоступен. Повторите попытку позже."
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDto createBranch(@RequestBody @Valid BranchCreateDto newBranch) {
        return branchService.createBranch(newBranch);
    }

    @Operation(
            summary = "Получение номеров отделений банка по номеру банка",
            description = "Возвращает список номеров отделений банка, соответствующих указанному номеру банка"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение списка номеров отделений",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = BranchBankNumberDTO.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверный формат номера банка"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Отделения с указанным номером банка не найдены"
            )
    })
    @Parameter(
            name = "bankNumber",
            description = "Номер банка для поиска отделений",
            required = true,
            example = "123456",
            schema = @Schema(type = "string", minLength = 1)
    )
    @GetMapping("/similar-numbers")
    public List<BranchBankNumberDTO> getBranchBankNumberListByBankNumber(@RequestParam String bankNumber) {
        return branchService.getBranchBankNumberByBankNumber(bankNumber);
    }

    @Operation(
            summary = "Архивировать отделения",
            description = "Переводит указанные отделения в архивное состояние (помечает как закрытые). " +
                    "Для архивирования передается массив UUID отделений."
            ,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Отделения успешно архивированы",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ArchiveBranchResponse.class))
                            )),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Одно или несколько отделений не найдены",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Уже архивировано",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                    )
                    )


            })
    @PutMapping("/archive")
    public ArchiveBranchResponse archiveBranch(@RequestBody BranchIdListDto request) throws BadRequestException {
        return branchService.archiveBranchesId(request.branchIds());
    }
}