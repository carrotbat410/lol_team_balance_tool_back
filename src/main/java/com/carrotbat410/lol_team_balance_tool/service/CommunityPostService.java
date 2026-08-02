package com.carrotbat410.lol_team_balance_tool.service;

import com.carrotbat410.lol_team_balance_tool.dto.CommunityPostRequestDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.CommunityPostPageResponseDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.CommunityPostResponseDTO;
import com.carrotbat410.lol_team_balance_tool.entity.CommunityPostCategory;
import com.carrotbat410.lol_team_balance_tool.entity.CommunityPostEntity;
import com.carrotbat410.lol_team_balance_tool.exHandler.exception.NotFoundDataException;
import com.carrotbat410.lol_team_balance_tool.exHandler.exception.UnprocessableContentException;
import com.carrotbat410.lol_team_balance_tool.repository.CommunityPostRepository;
import com.carrotbat410.lol_team_balance_tool.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityPostService {

    private final CommunityPostRepository communityPostRepository;
    private final CommunitySettingService communitySettingService;

    public List<CommunityPostResponseDTO> getPosts(CommunityPostCategory category) {
        return communityPostRepository.findByCategoryOrderByNoDesc(category).stream()
                .map(CommunityPostResponseDTO::new)
                .toList();
    }

    public CommunityPostPageResponseDTO getPostPage(CommunityPostCategory category, int page, int size) {
        PageRequest pageRequest = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50));
        Page<CommunityPostResponseDTO> posts = communityPostRepository.findByCategoryOrderByNoDesc(category, pageRequest)
                .map(CommunityPostResponseDTO::new);
        List<CommunityPostResponseDTO> notices = getPinnedNotices(category);

        return new CommunityPostPageResponseDTO(notices, posts);
    }

    public CommunityPostPageResponseDTO getVisiblePostPage(CommunityPostCategory category, int page, int size) {
        if (!SecurityUtils.isCurrentUserAdmin() && !communitySettingService.isVisibleToUsers()) {
            throw new AccessDeniedException("커뮤니티가 아직 공개되지 않았습니다.");
        }

        return getPostPage(category, page, size);
    }

    @Transactional
    public CommunityPostResponseDTO getVisiblePost(Long postNo) {
        CommunityPostEntity post = findPost(postNo);
        if (!canReadPost(post)) {
            throw new AccessDeniedException("게시글을 볼 권한이 없습니다.");
        }

        post.setViewCount(post.getViewCount() + 1);

        return new CommunityPostResponseDTO(communityPostRepository.save(post));
    }

    @Transactional
    public CommunityPostResponseDTO createPost(CommunityPostRequestDTO request) {
        CommunityPostEntity post = new CommunityPostEntity();
        post.setCategory(request.getCategory());
        post.setTitle(request.getTitle().trim());
        post.setContent(request.getContent().trim());
        post.setImageUrl(null);
        post.setWriterId(SecurityUtils.getCurrentUserIdFromAuthentication());
        post.setViewCount(0);

        return new CommunityPostResponseDTO(communityPostRepository.save(post));
    }

    @Transactional
    public CommunityPostResponseDTO createUserPost(CommunityPostRequestDTO request) {
        validateUserCanWriteRecruit(request.getCategory());
        return createPost(request);
    }

    @Transactional
    public CommunityPostResponseDTO updatePost(Long postNo, CommunityPostRequestDTO request) {
        CommunityPostEntity post = findPost(postNo);
        post.setCategory(request.getCategory());
        post.setTitle(request.getTitle().trim());
        post.setContent(request.getContent().trim());
        post.setImageUrl(null);

        return new CommunityPostResponseDTO(communityPostRepository.save(post));
    }

    @Transactional
    public CommunityPostResponseDTO updateUserPost(Long postNo, CommunityPostRequestDTO request) {
        CommunityPostEntity post = findPost(postNo);
        if (SecurityUtils.isCurrentUserAdmin()) {
            return updatePost(postNo, request);
        }

        validateUserCanWriteRecruit(request.getCategory());
        validateUserOwnsRecruitPost(post);

        post.setTitle(request.getTitle().trim());
        post.setContent(request.getContent().trim());
        post.setImageUrl(null);

        return new CommunityPostResponseDTO(communityPostRepository.save(post));
    }

    @Transactional
    public void deletePost(Long postNo) {
        CommunityPostEntity post = findPost(postNo);
        communityPostRepository.delete(post);
    }

    @Transactional
    public void deleteUserPost(Long postNo) {
        CommunityPostEntity post = findPost(postNo);
        if (!SecurityUtils.isCurrentUserAdmin()) {
            validateUserOwnsRecruitPost(post);
        }

        communityPostRepository.delete(post);
    }

    private CommunityPostEntity findPost(Long postNo) {
        return communityPostRepository.findById(postNo)
                .orElseThrow(() -> new NotFoundDataException("게시글을 찾을 수 없습니다."));
    }

    private void validateUserCanWriteRecruit(CommunityPostCategory category) {
        if (SecurityUtils.isCurrentUserAdmin()) {
            return;
        }

        if (!communitySettingService.isVisibleToUsers()) {
            throw new AccessDeniedException("커뮤니티가 아직 공개되지 않았습니다.");
        }

        if (category != CommunityPostCategory.RECRUIT) {
            throw new UnprocessableContentException("INVALID_COMMUNITY_CATEGORY", "일반 사용자는 내전모집 글만 작성할 수 있습니다.");
        }
    }

    private void validateUserOwnsRecruitPost(CommunityPostEntity post) {
        String currentUserId = SecurityUtils.getCurrentUserIdFromAuthentication();
        if (post.getCategory() != CommunityPostCategory.RECRUIT || !currentUserId.equals(post.getWriterId())) {
            throw new AccessDeniedException("본인이 작성한 내전모집 글만 변경할 수 있습니다.");
        }
    }

    private List<CommunityPostResponseDTO> getPinnedNotices(CommunityPostCategory category) {
        if (category == CommunityPostCategory.NOTICE) {
            return List.of();
        }

        int noticeDisplayCount = communitySettingService.getSetting().getNoticeDisplayCount();
        if (noticeDisplayCount <= 0) {
            return List.of();
        }

        return communityPostRepository
                .findByCategoryOrderByNoDesc(CommunityPostCategory.NOTICE, PageRequest.of(0, noticeDisplayCount))
                .stream()
                .map(CommunityPostResponseDTO::new)
                .toList();
    }

    private boolean canReadPost(CommunityPostEntity post) {
        if (SecurityUtils.isCurrentUserAdmin() || communitySettingService.isVisibleToUsers()) {
            return true;
        }

        String currentUserId = SecurityUtils.getCurrentUserIdOrNull();
        return currentUserId != null && currentUserId.equals(post.getWriterId());
    }
}
