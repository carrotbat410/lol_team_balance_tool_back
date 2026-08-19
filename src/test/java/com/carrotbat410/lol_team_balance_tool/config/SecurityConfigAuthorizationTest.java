package com.carrotbat410.lol_team_balance_tool.config;

import com.carrotbat410.lol_team_balance_tool.controller.AdminController;
import com.carrotbat410.lol_team_balance_tool.controller.CommunitySettingController;
import com.carrotbat410.lol_team_balance_tool.controller.CommunityPostController;
import com.carrotbat410.lol_team_balance_tool.dto.response.CommunityPostViewResponseDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.CommunitySettingResponseDTO;
import com.carrotbat410.lol_team_balance_tool.jwt.JWTUtil;
import com.carrotbat410.lol_team_balance_tool.service.AdminUserService;
import com.carrotbat410.lol_team_balance_tool.service.CommunitySettingService;
import com.carrotbat410.lol_team_balance_tool.service.CommunityPostService;
import com.carrotbat410.lol_team_balance_tool.service.CommunityPostViewService;
import com.carrotbat410.lol_team_balance_tool.service.VisitorLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AdminController.class, CommunitySettingController.class, CommunityPostController.class})
@Import(SecurityConfig.class)
class SecurityConfigAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JWTUtil jwtUtil;

    @MockBean
    private AdminUserService adminUserService;

    @MockBean
    private VisitorLogService visitorLogService;

    @MockBean
    private CommunitySettingService communitySettingService;

    @MockBean
    private CommunityPostService communityPostService;

    @MockBean
    private CommunityPostViewService communityPostViewService;

    @Test
    @WithMockUser(roles = "OPERATOR")
    void operatorCanAccessOperatorOnlyAdminReads() throws Exception {
        when(adminUserService.getUsers()).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/visits/summary"))
                .andExpect(status().isOk());

        mockMvc.perform(head("/api/admin/users"))
                .andExpect(status().isOk());

        mockMvc.perform(head("/api/admin/visits/summary"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCannotAccessOperatorOnlyAdminReads() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/admin/visits/summary"))
                .andExpect(status().isForbidden());

        mockMvc.perform(head("/api/admin/users"))
                .andExpect(status().isForbidden());

        mockMvc.perform(head("/api/admin/visits/summary"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCannotChangeRoles() throws Exception {
        mockMvc.perform(patch("/api/admin/users/1/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"ROLE_USER\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void operatorCanChangeRolesAndCommunitySetting() throws Exception {
        when(communitySettingService.updateSetting(any()))
                .thenReturn(new CommunitySettingResponseDTO(true, 2));

        mockMvc.perform(patch("/api/admin/users/1/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"ROLE_ADMIN\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/admin/community/settings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"visibleToUsers\":true,\"noticeDisplayCount\":2}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCannotAccessAdminRoutes() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void anonymousCanRecordViewOnlyOnExactPublicPostPath() throws Exception {
        when(communityPostViewService.recordView(any(), any()))
                .thenReturn(new CommunityPostViewResponseDTO(true, 1));

        mockMvc.perform(post("/api/community/posts/1/views")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"visitorId\":\"guest_123\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/community/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"category\":\"RECRUIT\",\"title\":\"title\",\"content\":\"content\"}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/community/posts/1/views/extra")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"visitorId\":\"guest_123\"}"))
                .andExpect(status().isForbidden());
    }
}
