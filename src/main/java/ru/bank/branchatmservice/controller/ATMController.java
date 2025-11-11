package ru.bank.branchatmservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.bank.branchatmservice.dto.ATMFullDto;
import ru.bank.branchatmservice.dto.InfoAtmDto;
import ru.bank.branchatmservice.dto.request.AtmFilterDto;
import ru.bank.branchatmservice.dto.request.UpdateAtmInfoRequest;
import ru.bank.branchatmservice.dto.response.AtmFilterResponseDto;
import ru.bank.branchatmservice.dto.response.InfoDeletionArchivingAtmResponse;
import ru.bank.branchatmservice.dto.request.AtmCreateDto;
import ru.bank.branchatmservice.dto.response.MessageResponseDto;
import ru.bank.branchatmservice.service.ATMService;

import java.util.List;
import java.util.UUID;

/**
 * Контроллер для управления банкоматами.
 * Предоставляет API для создания, поиска, обновления и архивации банкоматов.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/atms")
public class ATMController {

    private final ATMService atmService;

    @Operation(
            summary = "Просмотр информации о конкретном банкомате по UUID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешное получение информации",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ATMFullDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Данные не найдены.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(example = """
                                            {
                                                "uri": "http://localhost:8280/api/v1/atms/fc14fc78-c515-4256-84ac-924047d41212",
                                                "status": "404 NOT FOUND",
                                                "message": "Данные не найдены",
                                                "timestamp": "2025-09-12T09:52:03.1323482"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "503",
                            description = "Сервер временно недоступен по техническим причинам. Попробуйте позже.",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
            }
    )
    @GetMapping("/{atmId}")
    public ATMFullDto getATMById(
            @Parameter(
                    name = "atmId",
                    in = ParameterIn.PATH,
                    description = "Уникальный UUID банкомата",
                    required = true,
                    example = "fc14fc78-c515-4256-84ac-924047d41211",
                    schema = @Schema(type = "string", format = "uuid")
            )
            @PathVariable(name = "atmId") UUID atmId) {
        return atmService.getATMById(atmId);
    }

    @Operation(
            summary = "Поиск списка банкоматов по фильтру",
            description = "Выполняет поиск списка банкоматов по фильтру. Возвращает список банкоматов удовлетворяющих фильтру."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Банкоматы успешно найдены",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = AtmFilterResponseDto.class))
                    )
            )
    })
    @GetMapping("/filter")
    public List<AtmFilterResponseDto> getAtmsByFilter(
            @ParameterObject AtmFilterDto filters) {
        return atmService.getAtmListByFilter(filters);
    }

    @Operation(
            summary = "Мягкое удаление банкоматов по идентификаторам",
            description = "Выполняет архивирование (soft delete) банкоматов по списку UUID. Возвращает информацию об удаленных банкоматах."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Банкоматы успешно архивированы",
                    content = @Content(
                            schema = @Schema(implementation = InfoAtmDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверные параметры запроса"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Банкоматы не найдены"
            )
    })
    @PutMapping("/archive")
    public List<InfoAtmDto> softDeleteATMByIds(
            @Parameter(
                    description = "Список UUID банкоматов для архивирования",
                    required = true,
                    example = "[\"550e8400-e29b-41d4-a716-446655440000\", \"6ba7b810-9dad-11d1-80b4-00c04fd430c8\"]"
            )
            @RequestParam List<UUID> atmIds) {
        return atmService.softDeleteATMByIds(atmIds);
    }

    @Operation(
            summary = "Архивация банкомата",
            description = "Переводит банкомат в архивный статус (неактивное состояние)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Банкомат успешно архивирован",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = InfoDeletionArchivingAtmResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверный запрос",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(example = """
                                    {
                                      "timestamp": "2024-01-15T10:30:00Z",
                                      "status": 400,
                                      "error": "Bad Request",
                                      "message": "Неверный формат ID банкомата",
                                      "path": "/api/v1/atms/invalid-id/archive"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Банкомат не найден",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(example = """
                                    {
                                      "timestamp": "2024-01-15T10:30:00Z",
                                      "status": 404,
                                      "error": "Not Found",
                                      "message": "Банкомат с ID 123 не найден",
                                      "path": "/api/v1/atms/123/archive"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Банкомат уже архивирован",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(example = """
                                    {
                                      "timestamp": "2024-01-15T10:30:00Z",
                                      "status": 409,
                                      "error": "Conflict",
                                      "message": "Банкомат уже находится в архиве",
                                      "path": "/api/v1/atms/123/archive"
                                    }
                                    """)
                    )
            )
    })
    @PutMapping("/{atmId}/archive")
    public ResponseEntity<InfoDeletionArchivingAtmResponse> archiveAtm(
            @Parameter(description = "ID банкомата", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID atmId) {

        InfoDeletionArchivingAtmResponse response = atmService.archiveAtm(atmId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Добавление банкомата",
            description = "Добавляет банкомат"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Банкомат успешно добавлен",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Неверный запрос",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(example = """
                                    {
                                      "timestamp": "2025-01-15T10:30:00Z",
                                      "status": 404,
                                      "error": "Bad Request",
                                      "message": "Отделение с номером 101 не найдено",
                                      "path": "/api/v1/atms"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Банкомат не найден",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(example = """
                                    {
                                      "timestamp": "2025-01-15T10:30:00Z",
                                      "status": 404,
                                      "error": "Not Found",
                                      "message": "Город с наименованием Питер не найден",
                                      "path": "/api/v1/atms"
                                    }
                                    """)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<MessageResponseDto> createATM(@RequestBody AtmCreateDto atmCreateDto) {
        return new ResponseEntity<>(atmService.createATM(atmCreateDto), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Просмотр списка банкоматов",
            description = "Возвращает список банкоматов"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Данные успешно получены",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(
                                    schema = @Schema(implementation = AtmFilterResponseDto.class))
                    )
            )
    })
    @GetMapping
    public List<AtmFilterResponseDto> findAtms() {
        return atmService.getAllAtmList();
    }

    @Operation(
            summary = "Обновление информации о банкомате"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Банкомат успешно отредактирован"
            )
    })
    @PutMapping("/{atmId}")
    public ResponseEntity updateATMById(@PathVariable("atmId") UUID atmId,
                                     @RequestBody UpdateAtmInfoRequest updateAtmInfoRequest) {
        atmService.updateATM(atmId, updateAtmInfoRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
