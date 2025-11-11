package ru.bank.branchatmservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bank.branchatmservice.dto.BranchShortDto;
import ru.bank.branchatmservice.dto.response.BranchNameResponse;
import ru.bank.branchatmservice.service.BranchService;

import java.util.List;
import java.util.UUID;

/**
 * Контроллер для операций с отделениями и банкоматами.
 * Предоставляет вспомогательные API для работы со связанными сущностями.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/branch_ATMs")
public class BranchATMController {
    private final BranchService branchService;

    @Operation(
            summary = "Находит список отделений по id отдела",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешное получение информации",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = BranchShortDto.class)))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Данные не найдены.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(example = """
                                            {
                                              "uri": "http://localhost:8280/api/v1/branch_ATMs/branch/94d3f9a2-fb96-45c0-bf4d-0ef2a174db02",
                                              "status": "404 NOT FOUND",
                                              "message": "Данные не найдены.",
                                              "timestamp": "2025-09-05T16:48:01.0689418"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "503",
                            description = "Сервис временно недоступен. Повторите попытку позже."
                    ),
            }
    )
    @GetMapping("/branch/{departmentId}")
    public List<BranchShortDto> findBranchesByDepartmentId(
            @Parameter(
                    name = "departmentId",
                    in = ParameterIn.PATH,
                    description = "UUID отдела",
                    required = true,
                    example = "94d3f9a2-fb96-45c0-bf4d-0ef2a174db01",
                    schema = @Schema(type = "string", format = "uuid")
            )
            @PathVariable(name = "departmentId") UUID departmentId) {
        return branchService.findBranchesByDepartmentId(departmentId);
    }

    @Operation(
            summary = "Получение имени отделения по UUID отделения",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешное получение информации",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = BranchNameResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Ошибка авторизации. Для доступа к запрашиваемому ресурсу требуется аутентификация. ",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Данные не найдены.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(example = """
                                            {
                                                "uri": "http://localhost:8280/api/v1/branch_ATMs/bb2f1a60-6b13-45d8-b10a-316782389b3a/branchName",
                                                "status": "404 NOT FOUND",
                                                "message": "Запрашиваемые данные не найдены.",
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
    @GetMapping("/{bankBranchId}/branchName")
    public BranchNameResponse getBranchNameByBranchId(
            @Parameter(
                    name = "bankBranchId",
                    in = ParameterIn.PATH,
                    description = "UUID отделения",
                    required = true,
                    example = "bb2f1a60-6b13-45d8-b10a-316782389b3e",
                    schema = @Schema(type = "string", format = "uuid")
            )
            @PathVariable("bankBranchId") UUID bankBranchId) {
        return branchService.getBranchNameByBranchId(bankBranchId);
    }
}
