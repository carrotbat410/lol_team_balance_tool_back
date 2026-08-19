package com.carrotbat410.lol_team_balance_tool.controller;

import com.carrotbat410.lol_team_balance_tool.dto.response.CommunityPostViewResponseDTO;
import com.carrotbat410.lol_team_balance_tool.service.CommunityPostService;
import com.carrotbat410.lol_team_balance_tool.service.CommunityPostViewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommunityPostController.class)
@AutoConfigureMockMvc(addFilters = false)
class CommunityPostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommunityPostService communityPostService;

    @MockBean
    private CommunityPostViewService communityPostViewService;

    @Test
    void recordViewReturnsProjectEnvelope() throws Exception {
        when(communityPostViewService.recordView(eq(11L), any()))
                .thenReturn(new CommunityPostViewResponseDTO(true, 4));

        mockMvc.perform(post("/api/community/posts/11/views")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"visitorId\":\"guest_123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("community post view recorded"))
                .andExpect(jsonPath("$.data.counted").value(true))
                .andExpect(jsonPath("$.data.viewCount").value(4));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "invalid visitor!",
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
    })
    void invalidVisitorIdIsRejectedBeforeService(String visitorId) throws Exception {
        mockMvc.perform(post("/api/community/posts/11/views")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"visitorId\":\"" + visitorId + "\"}"))
                .andExpect(status().isBadRequest());

        verify(communityPostViewService, never()).recordView(any(), any());
    }
}
