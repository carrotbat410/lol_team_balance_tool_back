package com.carrotbat410.lol_team_balance_tool.service;

import com.carrotbat410.lol_team_balance_tool.dto.ChangePasswordRequestDTO;
import com.carrotbat410.lol_team_balance_tool.dto.CustomUserDetails;
import com.carrotbat410.lol_team_balance_tool.dto.DeleteAccountRequestDTO;
import com.carrotbat410.lol_team_balance_tool.entity.UserEntity;
import com.carrotbat410.lol_team_balance_tool.exHandler.exception.NotFoundDataException;
import com.carrotbat410.lol_team_balance_tool.exHandler.exception.UnprocessableContentException;
import com.carrotbat410.lol_team_balance_tool.repository.CommunityCommentRepository;
import com.carrotbat410.lol_team_balance_tool.repository.CommunityPostRepository;
import com.carrotbat410.lol_team_balance_tool.repository.SummonerRepository;
import com.carrotbat410.lol_team_balance_tool.repository.UserRepository;
import com.carrotbat410.lol_team_balance_tool.repository.VisitorLogRepository;
import com.carrotbat410.lol_team_balance_tool.utils.SecurityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SummonerRepository summonerRepository;

    @Mock
    private VisitorLogRepository visitorLogRepository;

    @Mock
    private CommunityPostRepository communityPostRepository;

    @Mock
    private CommunityCommentRepository communityCommentRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService(
                userRepository,
                summonerRepository,
                visitorLogRepository,
                communityPostRepository,
                communityCommentRepository,
                bCryptPasswordEncoder
        );
        authenticate("tester");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void changePasswordUpdatesManagedUserPassword() {
        UserEntity user = user(SecurityUtils.ROLE_USER);
        ChangePasswordRequestDTO request = changePasswordRequest("current-password", "new-password", "new-password");
        when(userRepository.findByUserId("tester")).thenReturn(user);
        when(bCryptPasswordEncoder.matches("current-password", "encoded-current")).thenReturn(true);
        when(bCryptPasswordEncoder.matches("new-password", "encoded-current")).thenReturn(false);
        when(bCryptPasswordEncoder.encode("new-password")).thenReturn("encoded-new");

        accountService.changePassword(request);

        assertThat(user.getPassword()).isEqualTo("encoded-new");
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void changePasswordRejectsCurrentPasswordMismatch() {
        UserEntity user = user(SecurityUtils.ROLE_USER);
        when(userRepository.findByUserId("tester")).thenReturn(user);
        when(bCryptPasswordEncoder.matches("wrong-password", "encoded-current")).thenReturn(false);

        assertThatThrownBy(() -> accountService.changePassword(
                changePasswordRequest("wrong-password", "new-password", "new-password")))
                .isInstanceOfSatisfying(UnprocessableContentException.class,
                        exception -> assertThat(exception.getCode()).isEqualTo("CURRENT_PASSWORD_MISMATCH"));

        verify(bCryptPasswordEncoder, never()).encode(any());
    }

    @Test
    void changePasswordRejectsCurrentPasswordOverSeventyTwoUtf8BytesAsMismatch() {
        UserEntity user = user(SecurityUtils.ROLE_USER);
        String overlongCurrentPassword = "가".repeat(25);
        when(userRepository.findByUserId("tester")).thenReturn(user);

        assertThatThrownBy(() -> accountService.changePassword(
                changePasswordRequest(overlongCurrentPassword, "new-password", "new-password")))
                .isInstanceOfSatisfying(UnprocessableContentException.class, exception -> {
                    assertThat(exception.getCode()).isEqualTo("CURRENT_PASSWORD_MISMATCH");
                    assertThat(exception.getMessage()).isEqualTo("현재 비밀번호가 일치하지 않습니다.");
                });

        verify(bCryptPasswordEncoder, never()).matches(any(), any());
        verify(bCryptPasswordEncoder, never()).encode(any());
    }

    @Test
    void changePasswordAcceptsNewPasswordAtSeventyTwoUtf8ByteBoundary() {
        UserEntity user = user(SecurityUtils.ROLE_USER);
        String boundaryPassword = "가".repeat(24);
        ChangePasswordRequestDTO request = changePasswordRequest(
                "current-password",
                boundaryPassword,
                boundaryPassword
        );
        when(userRepository.findByUserId("tester")).thenReturn(user);
        when(bCryptPasswordEncoder.matches("current-password", "encoded-current")).thenReturn(true);
        when(bCryptPasswordEncoder.matches(boundaryPassword, "encoded-current")).thenReturn(false);
        when(bCryptPasswordEncoder.encode(boundaryPassword)).thenReturn("encoded-new");

        accountService.changePassword(request);

        assertThat(user.getPassword()).isEqualTo("encoded-new");
    }

    @Test
    void changePasswordAcceptsSixEmojiCodePointsWithinBcryptByteLimit() {
        UserEntity user = user(SecurityUtils.ROLE_USER);
        String boundaryPassword = "😀".repeat(6);
        ChangePasswordRequestDTO request = changePasswordRequest(
                "current-password",
                boundaryPassword,
                boundaryPassword
        );
        when(userRepository.findByUserId("tester")).thenReturn(user);
        when(bCryptPasswordEncoder.matches("current-password", "encoded-current")).thenReturn(true);
        when(bCryptPasswordEncoder.matches(boundaryPassword, "encoded-current")).thenReturn(false);
        when(bCryptPasswordEncoder.encode(boundaryPassword)).thenReturn("encoded-new");

        accountService.changePassword(request);

        assertThat(boundaryPassword.codePointCount(0, boundaryPassword.length())).isEqualTo(6);
        assertThat(boundaryPassword.getBytes(StandardCharsets.UTF_8)).hasSize(24);
        assertThat(user.getPassword()).isEqualTo("encoded-new");
    }

    @Test
    void changePasswordRejectsNewPasswordUnderSixUnicodeCodePoints() {
        UserEntity user = user(SecurityUtils.ROLE_USER);
        String shortPassword = "😀".repeat(3);
        when(userRepository.findByUserId("tester")).thenReturn(user);
        when(bCryptPasswordEncoder.matches("current-password", "encoded-current")).thenReturn(true);

        assertThatThrownBy(() -> accountService.changePassword(
                changePasswordRequest("current-password", shortPassword, shortPassword)))
                .isInstanceOfSatisfying(UnprocessableContentException.class, exception -> {
                    assertThat(exception.getCode()).isEqualTo("PASSWORD_LENGTH_INVALID");
                    assertThat(exception.getMessage())
                            .isEqualTo("새 비밀번호는 6자 이상 72자 이하로 입력해주세요.");
                });

        verify(bCryptPasswordEncoder, never()).matches(shortPassword, "encoded-current");
        verify(bCryptPasswordEncoder, never()).encode(any());
    }

    @Test
    void changePasswordRejectsNewPasswordOverSeventyTwoUnicodeCodePointsBeforeByteLimit() {
        UserEntity user = user(SecurityUtils.ROLE_USER);
        String overlongPassword = "😀".repeat(73);
        when(userRepository.findByUserId("tester")).thenReturn(user);
        when(bCryptPasswordEncoder.matches("current-password", "encoded-current")).thenReturn(true);

        assertThatThrownBy(() -> accountService.changePassword(
                changePasswordRequest("current-password", overlongPassword, overlongPassword)))
                .isInstanceOfSatisfying(UnprocessableContentException.class, exception -> {
                    assertThat(exception.getCode()).isEqualTo("PASSWORD_LENGTH_INVALID");
                    assertThat(exception.getMessage())
                            .isEqualTo("새 비밀번호는 6자 이상 72자 이하로 입력해주세요.");
                });

        verify(bCryptPasswordEncoder, never()).matches(overlongPassword, "encoded-current");
        verify(bCryptPasswordEncoder, never()).encode(any());
    }

    @Test
    void changePasswordRejectsPasswordConfirmationUnderSixUnicodeCodePoints() {
        UserEntity user = user(SecurityUtils.ROLE_USER);
        String shortPasswordConfirm = "😀".repeat(3);
        when(userRepository.findByUserId("tester")).thenReturn(user);
        when(bCryptPasswordEncoder.matches("current-password", "encoded-current")).thenReturn(true);

        assertThatThrownBy(() -> accountService.changePassword(
                changePasswordRequest("current-password", "new-password", shortPasswordConfirm)))
                .isInstanceOfSatisfying(UnprocessableContentException.class, exception -> {
                    assertThat(exception.getCode()).isEqualTo("PASSWORD_LENGTH_INVALID");
                    assertThat(exception.getMessage())
                            .isEqualTo("새 비밀번호는 6자 이상 72자 이하로 입력해주세요.");
                });

        verify(bCryptPasswordEncoder, never()).matches("new-password", "encoded-current");
        verify(bCryptPasswordEncoder, never()).encode(any());
    }

    @Test
    void changePasswordRejectsPasswordConfirmationOverSeventyTwoUnicodeCodePointsBeforeByteLimit() {
        UserEntity user = user(SecurityUtils.ROLE_USER);
        String overlongPasswordConfirm = "😀".repeat(73);
        when(userRepository.findByUserId("tester")).thenReturn(user);
        when(bCryptPasswordEncoder.matches("current-password", "encoded-current")).thenReturn(true);

        assertThatThrownBy(() -> accountService.changePassword(
                changePasswordRequest("current-password", "new-password", overlongPasswordConfirm)))
                .isInstanceOfSatisfying(UnprocessableContentException.class, exception -> {
                    assertThat(exception.getCode()).isEqualTo("PASSWORD_LENGTH_INVALID");
                    assertThat(exception.getMessage())
                            .isEqualTo("새 비밀번호는 6자 이상 72자 이하로 입력해주세요.");
                });

        verify(bCryptPasswordEncoder, never()).matches("new-password", "encoded-current");
        verify(bCryptPasswordEncoder, never()).encode(any());
    }

    @Test
    void changePasswordRejectsNewPasswordOverSeventyTwoUtf8Bytes() {
        UserEntity user = user(SecurityUtils.ROLE_USER);
        String overlongPassword = "가".repeat(25);
        when(userRepository.findByUserId("tester")).thenReturn(user);
        when(bCryptPasswordEncoder.matches("current-password", "encoded-current")).thenReturn(true);

        assertThatThrownBy(() -> accountService.changePassword(
                changePasswordRequest("current-password", overlongPassword, overlongPassword)))
                .isInstanceOfSatisfying(UnprocessableContentException.class, exception -> {
                    assertThat(exception.getCode()).isEqualTo("PASSWORD_TOO_LONG");
                    assertThat(exception.getMessage())
                            .isEqualTo("새 비밀번호는 UTF-8 기준 72바이트 이하로 입력해주세요.");
                });

        verify(bCryptPasswordEncoder, never()).matches(overlongPassword, "encoded-current");
        verify(bCryptPasswordEncoder, never()).encode(any());
    }

    @Test
    void changePasswordRejectsPasswordConfirmationOverSeventyTwoUtf8Bytes() {
        UserEntity user = user(SecurityUtils.ROLE_USER);
        String overlongPasswordConfirm = "가".repeat(25);
        when(userRepository.findByUserId("tester")).thenReturn(user);
        when(bCryptPasswordEncoder.matches("current-password", "encoded-current")).thenReturn(true);

        assertThatThrownBy(() -> accountService.changePassword(
                changePasswordRequest("current-password", "new-password", overlongPasswordConfirm)))
                .isInstanceOfSatisfying(UnprocessableContentException.class, exception -> {
                    assertThat(exception.getCode()).isEqualTo("PASSWORD_TOO_LONG");
                    assertThat(exception.getMessage())
                            .isEqualTo("새 비밀번호는 UTF-8 기준 72바이트 이하로 입력해주세요.");
                });

        verify(bCryptPasswordEncoder, never()).matches("new-password", "encoded-current");
        verify(bCryptPasswordEncoder, never()).encode(any());
    }

    @Test
    void changePasswordRejectsSamePassword() {
        UserEntity user = user(SecurityUtils.ROLE_USER);
        when(userRepository.findByUserId("tester")).thenReturn(user);
        when(bCryptPasswordEncoder.matches("current-password", "encoded-current")).thenReturn(true);

        assertThatThrownBy(() -> accountService.changePassword(
                changePasswordRequest("current-password", "current-password", "current-password")))
                .isInstanceOfSatisfying(UnprocessableContentException.class,
                        exception -> assertThat(exception.getCode()).isEqualTo("NEW_PASSWORD_SAME_AS_CURRENT"));

        verify(bCryptPasswordEncoder, never()).encode(any());
    }

    @Test
    void changePasswordRejectsPasswordConfirmationMismatch() {
        UserEntity user = user(SecurityUtils.ROLE_USER);
        when(userRepository.findByUserId("tester")).thenReturn(user);
        when(bCryptPasswordEncoder.matches("current-password", "encoded-current")).thenReturn(true);

        assertThatThrownBy(() -> accountService.changePassword(
                changePasswordRequest("current-password", "new-password", "different-password")))
                .isInstanceOfSatisfying(UnprocessableContentException.class,
                        exception -> assertThat(exception.getCode()).isEqualTo("PASSWORD_CONFIRMATION_MISMATCH"));

        verify(bCryptPasswordEncoder, never()).encode(any());
    }

    @Test
    void changePasswordRejectsMissingUser() {
        when(userRepository.findByUserId("tester")).thenReturn(null);

        assertThatThrownBy(() -> accountService.changePassword(
                changePasswordRequest("current-password", "new-password", "new-password")))
                .isInstanceOfSatisfying(NotFoundDataException.class,
                        exception -> assertThat(exception.getCode()).isEqualTo("USER_NOT_FOUND"));

        verify(bCryptPasswordEncoder, never()).matches(any(), any());
    }

    @ParameterizedTest
    @ValueSource(strings = {SecurityUtils.ROLE_ADMIN, SecurityUtils.ROLE_OPERATOR})
    void deleteMyAccountRejectsPrivilegedAccountBeforePasswordCheck(String role) {
        UserEntity user = user(role);
        when(userRepository.findByUserId("tester")).thenReturn(user);

        assertThatThrownBy(() -> accountService.deleteMyAccount(deleteAccountRequest("wrong-password")))
                .isInstanceOfSatisfying(UnprocessableContentException.class, exception -> {
                    assertThat(exception.getCode()).isEqualTo("PRIVILEGED_ACCOUNT_DELETE_NOT_ALLOWED");
                    assertThat(exception.getMessage()).isEqualTo("관리자 및 운영자 계정은 회원 탈퇴를 할 수 없습니다.");
        });

        verify(bCryptPasswordEncoder, never()).matches(any(), any());
        verify(summonerRepository, never()).deleteByUserId(any());
        verify(visitorLogRepository, never()).anonymizeUserId(any());
        verify(communityCommentRepository, never()).deleteByWriterId(any());
        verify(communityPostRepository, never()).deleteByWriterId(any());
        verify(userRepository, never()).delete(any());
    }

    @Test
    void deleteMyAccountRejectsPasswordOverSeventyTwoUtf8BytesAsMismatch() {
        UserEntity user = user(SecurityUtils.ROLE_USER);
        String overlongPassword = "가".repeat(25);
        when(userRepository.findByUserId("tester")).thenReturn(user);

        assertThatThrownBy(() -> accountService.deleteMyAccount(deleteAccountRequest(overlongPassword)))
                .isInstanceOfSatisfying(UnprocessableContentException.class, exception -> {
                    assertThat(exception.getCode()).isEqualTo("PASSWORD_MISMATCH");
                    assertThat(exception.getMessage()).isEqualTo("비밀번호가 일치하지 않습니다.");
        });

        verify(bCryptPasswordEncoder, never()).matches(any(), any());
        verifyNoInteractions(
                summonerRepository,
                visitorLogRepository,
                communityCommentRepository,
                communityPostRepository
        );
        verify(userRepository, never()).delete(any());
    }

    @Test
    void deleteMyAccountDeletesRegularUser() {
        UserEntity user = user(SecurityUtils.ROLE_USER);
        DeleteAccountRequestDTO request = deleteAccountRequest("current-password");
        when(userRepository.findByUserId("tester")).thenReturn(user);
        when(bCryptPasswordEncoder.matches("current-password", "encoded-current")).thenReturn(true);

        accountService.deleteMyAccount(request);

        verify(summonerRepository).deleteByUserId("tester");
        verify(visitorLogRepository).anonymizeUserId("tester");
        verify(communityCommentRepository).deleteByWriterId("tester");
        verify(communityPostRepository).deleteByWriterId("tester");
        verify(userRepository).delete(user);
    }

    private void authenticate(String userId) {
        UserEntity user = new UserEntity();
        user.setUserId(userId);
        user.setPassword("encoded-current");
        user.setRole(SecurityUtils.ROLE_USER);
        CustomUserDetails principal = new CustomUserDetails(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
        );
    }

    private UserEntity user(String role) {
        UserEntity user = new UserEntity();
        user.setUserId("tester");
        user.setPassword("encoded-current");
        user.setRole(role);
        return user;
    }

    private ChangePasswordRequestDTO changePasswordRequest(
            String currentPassword,
            String newPassword,
            String newPasswordConfirm
    ) {
        ChangePasswordRequestDTO request = new ChangePasswordRequestDTO();
        request.setCurrentPassword(currentPassword);
        request.setNewPassword(newPassword);
        request.setNewPasswordConfirm(newPasswordConfirm);
        return request;
    }

    private DeleteAccountRequestDTO deleteAccountRequest(String password) {
        DeleteAccountRequestDTO request = new DeleteAccountRequestDTO();
        request.setPassword(password);
        return request;
    }
}
