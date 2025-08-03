
package com.carrotbat410.lol_team_balance_tool.controller;

import com.carrotbat410.lol_team_balance_tool.dto.SummonerDTO;
import com.carrotbat410.lol_team_balance_tool.service.SummonerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SummonerController.class)
class SummonerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SummonerService summonerService;

    @Test
    @DisplayName("getMySummoners Paging Test")
    @WithMockUser(username = "testUser")
    void getMySummoners() throws Exception {
        // given
        List<SummonerDTO> summonerDTOList = new ArrayList<>();
        for (long i = 0L; i < 5; i++) {
            summonerDTOList.add(SummonerDTO.builder().no((long) i).summonerName("summoner" + i).tagLine("KR1").build());
        }
        PageRequest pageable = PageRequest.of(0, 5);
        PageImpl<SummonerDTO> summonerDTOPage = new PageImpl<>(summonerDTOList, pageable, 10);

        given(summonerService.findSummoners(any())).willReturn(summonerDTOPage);

        // when & then
        mockMvc.perform(get("/summoners")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(5))
                .andExpect(jsonPath("$.data.totalElements").value(10));
    }
}
