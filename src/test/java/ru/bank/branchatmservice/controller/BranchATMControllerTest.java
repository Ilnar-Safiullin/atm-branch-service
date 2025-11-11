package ru.bank.branchatmservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.bank.branchatmservice.dto.BranchShortDto;
import ru.bank.branchatmservice.dto.response.BranchNameResponse;
import ru.bank.branchatmservice.service.BranchService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BranchATMController.class)
public class BranchATMControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BranchService branchService;

    @Test
    @DisplayName("Должен вернуться список отделений")
    void shouldReturnShortBranches_ifIdValid() throws Exception {
        List<BranchShortDto> branches = List.of(new BranchShortDto(UUID.randomUUID(), "ДО «ГУМ»"));

        when(branchService.findBranchesByDepartmentId(any(UUID.class)))
                .thenReturn(branches);

        String result = mvc.perform(get("/api/v1/branch_ATMs/branch/{departmentId}", UUID.randomUUID())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertEquals(objectMapper.writeValueAsString(branches), result);
    }

    @Test
    @DisplayName("Должно вернуться имя отделения")
    void shouldReturnBranchName_ifIdValid() throws Exception {
        BranchNameResponse response = new BranchNameResponse("ДО «ГУМ»");

        when(branchService.getBranchNameByBranchId(any(UUID.class)))
                .thenReturn(response);

        String result = mvc.perform(get("/api/v1/branch_ATMs/{bankBranchId}/branchName", UUID.randomUUID())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertEquals(objectMapper.writeValueAsString(response), result);
    }
}
