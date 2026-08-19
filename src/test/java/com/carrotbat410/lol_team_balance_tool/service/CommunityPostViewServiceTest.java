package com.carrotbat410.lol_team_balance_tool.service;

import com.carrotbat410.lol_team_balance_tool.dto.CommunityPostViewRequestDTO;
import com.carrotbat410.lol_team_balance_tool.dto.CustomUserDetails;
import com.carrotbat410.lol_team_balance_tool.dto.response.CommunityPostViewResponseDTO;
import com.carrotbat410.lol_team_balance_tool.entity.CommunityPostEntity;
import com.carrotbat410.lol_team_balance_tool.entity.UserEntity;
import com.carrotbat410.lol_team_balance_tool.exHandler.exception.NotFoundDataException;
import com.carrotbat410.lol_team_balance_tool.repository.CommunityPostRepository;
import com.carrotbat410.lol_team_balance_tool.repository.CommunityPostViewRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommunityPostViewServiceTest {

    @Mock
    private CommunityPostService communityPostService;

    @Mock
    private CommunityPostRepository communityPostRepository;

    @Mock
    private CommunityPostViewRepository communityPostViewRepository;

    private CommunityPostViewService service;

    @BeforeEach
    void setUp() {
        service = new CommunityPostViewService(
                communityPostService,
                communityPostRepository,
                communityPostViewRepository
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void firstGuestViewIsCountedAndDuplicateIsNot() {
        CommunityPostViewRequestDTO request = request("guest_123");
        when(communityPostService.getVisiblePostEntity(1L)).thenReturn(new CommunityPostEntity());
        when(communityPostRepository.findViewCountByNoForUpdate(1L))
                .thenReturn(Optional.of(0L), Optional.of(1L));
        when(communityPostViewRepository.insertIgnore(anyLong(), any(), any(), any()))
                .thenReturn(1, 0);
        when(communityPostRepository.incrementViewCount(1L)).thenReturn(1);

        CommunityPostViewResponseDTO first = service.recordView(1L, request);
        CommunityPostViewResponseDTO duplicate = service.recordView(1L, request);

        assertThat(first).isEqualTo(new CommunityPostViewResponseDTO(true, 1));
        assertThat(duplicate).isEqualTo(new CommunityPostViewResponseDTO(false, 1));
        verify(communityPostRepository).incrementViewCount(1L);
    }

    @Test
    void signedInViewUsesServerUserIdentityAndStoresOnlyItsHash() throws Exception {
        authenticate("server-user");
        when(communityPostService.getVisiblePostEntity(1L)).thenReturn(new CommunityPostEntity());
        when(communityPostRepository.findViewCountByNoForUpdate(1L)).thenReturn(Optional.of(8L));
        when(communityPostViewRepository.insertIgnore(anyLong(), any(), any(), any())).thenReturn(1);
        when(communityPostRepository.incrementViewCount(1L)).thenReturn(1);
        ArgumentCaptor<String> hashCaptor = ArgumentCaptor.forClass(String.class);

        service.recordView(1L, request("client-controlled-id"));

        verify(communityPostViewRepository).insertIgnore(
                org.mockito.ArgumentMatchers.eq(1L),
                hashCaptor.capture(),
                any(),
                any()
        );
        assertThat(hashCaptor.getValue())
                .hasSize(64)
                .isEqualTo(sha256("user:server-user"))
                .doesNotContain("server-user", "client-controlled-id", "user:", "guest:");
    }

    @Test
    void locksParentBeforeInsertingViewAndIncrementingCount() {
        CommunityPostViewRequestDTO request = request("guest");
        when(communityPostService.getVisiblePostEntity(1L)).thenReturn(new CommunityPostEntity());
        when(communityPostRepository.findViewCountByNoForUpdate(1L)).thenReturn(Optional.of(4L));
        when(communityPostViewRepository.insertIgnore(anyLong(), any(), any(), any())).thenReturn(1);
        when(communityPostRepository.incrementViewCount(1L)).thenReturn(1);

        CommunityPostViewResponseDTO response = service.recordView(1L, request);

        assertThat(response).isEqualTo(new CommunityPostViewResponseDTO(true, 5));
        InOrder inOrder = inOrder(communityPostService, communityPostRepository, communityPostViewRepository);
        inOrder.verify(communityPostService).getVisiblePostEntity(1L);
        inOrder.verify(communityPostRepository).findViewCountByNoForUpdate(1L);
        inOrder.verify(communityPostViewRepository).insertIgnore(anyLong(), any(), any(), any());
        inOrder.verify(communityPostRepository).incrementViewCount(1L);
    }

    @Test
    void missingPostWhenLockingDoesNotInsertOrIncrement() {
        when(communityPostService.getVisiblePostEntity(4L)).thenReturn(new CommunityPostEntity());
        when(communityPostRepository.findViewCountByNoForUpdate(4L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.recordView(4L, request("guest")))
                .isInstanceOf(NotFoundDataException.class);

        verify(communityPostViewRepository, never()).insertIgnore(anyLong(), any(), any(), any());
        verify(communityPostRepository, never()).incrementViewCount(anyLong());
    }

    @Test
    void missingOrForbiddenPostDoesNotInsertOrIncrement() {
        when(communityPostService.getVisiblePostEntity(2L))
                .thenThrow(new NotFoundDataException("게시글을 찾을 수 없습니다."));
        when(communityPostService.getVisiblePostEntity(3L))
                .thenThrow(new AccessDeniedException("게시글을 볼 권한이 없습니다."));

        assertThatThrownBy(() -> service.recordView(2L, request("guest")))
                .isInstanceOf(NotFoundDataException.class);
        assertThatThrownBy(() -> service.recordView(3L, request("guest")))
                .isInstanceOf(AccessDeniedException.class);

        verify(communityPostViewRepository, never()).insertIgnore(anyLong(), any(), any(), any());
        verify(communityPostRepository, never()).findViewCountByNoForUpdate(anyLong());
        verify(communityPostRepository, never()).incrementViewCount(anyLong());
    }

    private CommunityPostViewRequestDTO request(String visitorId) {
        CommunityPostViewRequestDTO request = new CommunityPostViewRequestDTO();
        request.setVisitorId(visitorId);
        return request;
    }

    private void authenticate(String userId) {
        UserEntity user = new UserEntity();
        user.setUserId(userId);
        user.setPassword("password");
        user.setRole("ROLE_USER");
        CustomUserDetails principal = new CustomUserDetails(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
        );
    }

    private String sha256(String value) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
    }
}
