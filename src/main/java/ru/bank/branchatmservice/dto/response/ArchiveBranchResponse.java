package ru.bank.branchatmservice.dto.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.bank.branchatmservice.dto.AddressShortDto;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Ответ после архивирования отделения")
public class ArchiveBranchResponse {
    private List<ArchiveBranchesDto> archived;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class ArchiveBranchesDto {
        @Schema(
                description = "UUID отделения",
                example = "fedcba98-7654-3210-abcd-ef1234567890"
        )
        private UUID branchId;

        @Schema(
                description = "Название отделения",
                example = "ДО «ГУМ»"
        )
        private String name;
        @JsonUnwrapped
        private AddressShortDto address;
    }
}
