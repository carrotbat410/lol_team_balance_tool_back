package com.carrotbat410.lol_team_balance_tool.service;

import com.carrotbat410.lol_team_balance_tool.dto.CommunityCommentRequestDTO;
import com.carrotbat410.lol_team_balance_tool.dto.CommunityPostRequestDTO;
import com.carrotbat410.lol_team_balance_tool.dto.CustomUserDetails;
import com.carrotbat410.lol_team_balance_tool.entity.CommunityPostCategory;
import com.carrotbat410.lol_team_balance_tool.entity.CommunityPostEntity;
import com.carrotbat410.lol_team_balance_tool.entity.UserEntity;
import com.carrotbat410.lol_team_balance_tool.exHandler.exception.UnprocessableContentException;
import com.carrotbat410.lol_team_balance_tool.repository.CommunityCommentRepository;
import com.carrotbat410.lol_team_balance_tool.repository.CommunityPostRepository;
import com.carrotbat410.lol_team_balance_tool.utils.SecurityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CommunityAuthorizationServiceTest {

    @Mock
    private CommunityPostRepository postRepository;

    @Mock
    private CommunityCommentRepository commentRepository;

    @Mock
    private CommunitySettingService settingService;

    private CommunityPostService postService;
    private CommunityCommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        postService = new CommunityPostService(postRepository, settingService);
        commentService = new CommunityCommentService(commentRepository, postRepository, settingService);
        when(postRepository.findByCategoryOrderByNoDesc(any(), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));
        when(settingService.getSetting()).thenReturn(new com.carrotbat410.lol_team_balance_tool.dto.response.CommunitySettingResponseDTO(false, 0));
        when(postRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(commentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(postRepository.findById(1L)).thenReturn(Optional.of(new CommunityPostEntity()));
        when(commentRepository.findByPostNoOrderByNoAsc(1L)).thenReturn(List.of());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void operatorCanManagePrivateCommunity() {
        authenticate(SecurityUtils.ROLE_OPERATOR);
        when(settingService.isVisibleToUsers()).thenReturn(false);

        postService.createPost(postRequest(CommunityPostCategory.NOTICE));
        commentService.createComment(1L, commentRequest());

        verify(postRepository).save(any(CommunityPostEntity.class));
        verify(commentRepository).save(any());
    }

    @Test
    void adminCanReadButCannotWritePrivateCommunity() {
        authenticate(SecurityUtils.ROLE_ADMIN);
        when(settingService.isVisibleToUsers()).thenReturn(false);

        postService.getVisiblePostPage(CommunityPostCategory.RECRUIT, 0, 10);
        commentService.getComments(1L);

        assertThatThrownBy(() -> postService.createPost(postRequest(CommunityPostCategory.RECRUIT)))
                .isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> commentService.createComment(1L, commentRequest()))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void userCannotReadOrWritePrivateCommunity() {
        authenticate(SecurityUtils.ROLE_USER);
        when(settingService.isVisibleToUsers()).thenReturn(false);

        assertThatThrownBy(() -> postService.getVisiblePostPage(CommunityPostCategory.RECRUIT, 0, 10))
                .isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> postService.createUserPost(postRequest(CommunityPostCategory.RECRUIT)))
                .isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> commentService.getComments(1L))
                .isInstanceOf(AccessDeniedException.class);
        verify(postRepository, never()).save(any());
    }

    @Test
    void adminKeepsPublicManagementButCannotCreateNotice() {
        authenticate(SecurityUtils.ROLE_ADMIN);
        when(settingService.isVisibleToUsers()).thenReturn(true);

        postService.createPost(postRequest(CommunityPostCategory.RECRUIT));

        assertThatThrownBy(() -> postService.createPost(postRequest(CommunityPostCategory.NOTICE)))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void adminCannotChangeOrDeleteExistingNotice() {
        authenticate(SecurityUtils.ROLE_ADMIN);
        when(settingService.isVisibleToUsers()).thenReturn(true);
        CommunityPostEntity notice = new CommunityPostEntity();
        notice.setCategory(CommunityPostCategory.NOTICE);
        when(postRepository.findById(1L)).thenReturn(Optional.of(notice));

        assertThatThrownBy(() -> postService.updatePost(1L, postRequest(CommunityPostCategory.RECRUIT)))
                .isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> postService.deletePost(1L))
                .isInstanceOf(AccessDeniedException.class);

        verify(postRepository, never()).delete(notice);
    }

    @ParameterizedTest
    @ValueSource(strings = {"RECRUIT", "CLAN_PROMOTION"})
    void userCanWriteApprovedCategoriesInPublicCommunity(String category) {
        authenticate(SecurityUtils.ROLE_USER);
        when(settingService.isVisibleToUsers()).thenReturn(true);

        postService.createUserPost(postRequest(CommunityPostCategory.valueOf(category)));

        verify(postRepository).save(any(CommunityPostEntity.class));
    }

    @Test
    void userCanChangeOwnedRecruitPostToClanPromotion() {
        authenticate(SecurityUtils.ROLE_USER);
        when(settingService.isVisibleToUsers()).thenReturn(true);
        CommunityPostEntity post = new CommunityPostEntity();
        post.setCategory(CommunityPostCategory.RECRUIT);
        post.setWriterId("tester");
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.updateUserPost(1L, postRequest(CommunityPostCategory.CLAN_PROMOTION));

        assertThat(post.getCategory()).isEqualTo(CommunityPostCategory.CLAN_PROMOTION);
        verify(postRepository).save(post);
    }

    @Test
    void userCannotCreateNoticeInPublicCommunity() {
        authenticate(SecurityUtils.ROLE_USER);
        when(settingService.isVisibleToUsers()).thenReturn(true);

        assertThatThrownBy(() -> postService.createUserPost(postRequest(CommunityPostCategory.NOTICE)))
                .isInstanceOf(UnprocessableContentException.class);
    }

    private void authenticate(String role) {
        UserEntity user = new UserEntity();
        user.setUserId("tester");
        user.setPassword("password");
        user.setRole(role);
        CustomUserDetails principal = new CustomUserDetails(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
        );
    }

    private CommunityPostRequestDTO postRequest(CommunityPostCategory category) {
        CommunityPostRequestDTO request = new CommunityPostRequestDTO();
        request.setCategory(category);
        request.setTitle("title");
        request.setContent("content");
        return request;
    }

    private CommunityCommentRequestDTO commentRequest() {
        CommunityCommentRequestDTO request = new CommunityCommentRequestDTO();
        request.setContent("comment");
        return request;
    }
}
