package com.carrotbat410.lol_team_balance_tool.service;

import com.carrotbat410.lol_team_balance_tool.dto.AdminUserRoleUpdateRequestDTO;
import com.carrotbat410.lol_team_balance_tool.dto.CustomUserDetails;
import com.carrotbat410.lol_team_balance_tool.dto.DeleteAccountRequestDTO;
import com.carrotbat410.lol_team_balance_tool.dto.CommunitySettingUpdateRequestDTO;
import com.carrotbat410.lol_team_balance_tool.entity.UserEntity;
import com.carrotbat410.lol_team_balance_tool.exHandler.exception.UnprocessableContentException;
import com.carrotbat410.lol_team_balance_tool.repository.CommunityCommentRepository;
import com.carrotbat410.lol_team_balance_tool.repository.CommunityPostRepository;
import com.carrotbat410.lol_team_balance_tool.repository.CommunitySettingRepository;
import com.carrotbat410.lol_team_balance_tool.repository.SummonerRepository;
import com.carrotbat410.lol_team_balance_tool.repository.UserRepository;
import com.carrotbat410.lol_team_balance_tool.repository.VisitorLogRepository;
import com.carrotbat410.lol_team_balance_tool.utils.SecurityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OperatorProtectionServiceTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void lastOperatorCannotBeDemoted() {
        UserRepository userRepository = mock(UserRepository.class);
        UserEntity operator = user("operator", SecurityUtils.ROLE_OPERATOR);
        when(userRepository.findById(1)).thenReturn(Optional.of(operator));
        when(userRepository.findAllByRole(SecurityUtils.ROLE_OPERATOR)).thenReturn(List.of(operator));
        authenticate(operator);

        AdminUserRoleUpdateRequestDTO request = new AdminUserRoleUpdateRequestDTO();
        request.setRole(SecurityUtils.ROLE_ADMIN);
        AdminUserService service = new AdminUserService(userRepository, mock(JdbcTemplate.class));

        assertThatThrownBy(() -> service.updateRole(1, request))
                .isInstanceOf(UnprocessableContentException.class)
                .hasMessageContaining("마지막 운영자");
    }

    @Test
    void operatorCannotDeleteOwnAccount() {
        UserRepository userRepository = mock(UserRepository.class);
        UserEntity operator = user("operator", SecurityUtils.ROLE_OPERATOR);
        when(userRepository.findByUserId("operator")).thenReturn(operator);
        authenticate(operator);
        AccountService service = new AccountService(
                userRepository,
                mock(SummonerRepository.class),
                mock(VisitorLogRepository.class),
                mock(CommunityPostRepository.class),
                mock(CommunityCommentRepository.class),
                mock(BCryptPasswordEncoder.class)
        );

        assertThatThrownBy(() -> service.deleteMyAccount(new DeleteAccountRequestDTO()))
                .isInstanceOf(UnprocessableContentException.class)
                .hasMessageContaining("운영자 계정");
    }

    @Test
    void adminCannotBypassRoleChangeServicePermission() {
        UserEntity admin = user("admin", SecurityUtils.ROLE_ADMIN);
        authenticate(admin);
        AdminUserRoleUpdateRequestDTO request = new AdminUserRoleUpdateRequestDTO();
        request.setRole(SecurityUtils.ROLE_USER);
        AdminUserService service = new AdminUserService(mock(UserRepository.class), mock(JdbcTemplate.class));

        assertThatThrownBy(() -> service.updateRole(1, request))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void adminCannotBypassCommunitySettingServicePermission() {
        UserEntity admin = user("admin", SecurityUtils.ROLE_ADMIN);
        authenticate(admin);
        CommunitySettingUpdateRequestDTO request = new CommunitySettingUpdateRequestDTO();
        request.setVisibleToUsers(true);
        request.setNoticeDisplayCount(2);
        CommunitySettingService service = new CommunitySettingService(mock(CommunitySettingRepository.class));

        assertThatThrownBy(() -> service.updateSetting(request))
                .isInstanceOf(AccessDeniedException.class);
    }

    private void authenticate(UserEntity user) {
        CustomUserDetails principal = new CustomUserDetails(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
        );
    }

    private UserEntity user(String userId, String role) {
        UserEntity user = new UserEntity();
        user.setUserId(userId);
        user.setPassword("password");
        user.setRole(role);
        return user;
    }
}
