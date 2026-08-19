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
        return communityPostRepository.findByCategoryOrderByNoDesc(category);
    }

    public CommunityPostPageResponseDTO getPostPage(CommunityPostCategory category, int page, int size) {
        PageRequest pageRequest = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50));
        Page<CommunityPostResponseDTO> posts = communityPostRepository.findByCategoryOrderByNoDesc(category, pageRequest);
        List<CommunityPostResponseDTO> notices = getPinnedNotices(category);

        return new CommunityPostPageResponseDTO(notices, posts);
    }

    public CommunityPostPageResponseDTO getVisiblePostPage(CommunityPostCategory category, int page, int size) {
        if (!canReadCommunity()) {
            throw new AccessDeniedException("커뮤니티가 아직 공개되지 않았습니다.");
        }

        return getPostPage(category, page, size);
    }

    @Transactional(readOnly = true)
    public CommunityPostResponseDTO getVisiblePost(Long postNo) {
        return new CommunityPostResponseDTO(getVisiblePostEntity(postNo));
    }

    @Transactional
    public CommunityPostResponseDTO createPost(CommunityPostRequestDTO request) {
        validateAdminCanManageCommunity();
        validateNoticePermission(request.getCategory());
        return saveNewPost(request);
    }

    @Transactional
    public CommunityPostResponseDTO createUserPost(CommunityPostRequestDTO request) {
        if (SecurityUtils.isCurrentUserAdmin()) {
            return createPost(request);
        }

        validateUserCanWriteCommunity(request.getCategory());
        return saveNewPost(request);
    }

    @Transactional
    public CommunityPostResponseDTO updatePost(Long postNo, CommunityPostRequestDTO request) {
        validateAdminCanManageCommunity();
        CommunityPostEntity post = findPost(postNo);
        validateCanManageExistingNotice(post);
        validateNoticePermission(request.getCategory());
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

        validateUserCanWriteCommunity(request.getCategory());
        validateUserOwnsWritablePost(post);

        post.setCategory(request.getCategory());
        post.setTitle(request.getTitle().trim());
        post.setContent(request.getContent().trim());
        post.setImageUrl(null);

        return new CommunityPostResponseDTO(communityPostRepository.save(post));
    }

    @Transactional
    public void deletePost(Long postNo) {
        validateAdminCanManageCommunity();
        CommunityPostEntity post = findPost(postNo);
        validateCanManageExistingNotice(post);
        communityPostRepository.delete(post);
    }

    @Transactional
    public void deleteUserPost(Long postNo) {
        CommunityPostEntity post = findPost(postNo);
        if (SecurityUtils.isCurrentUserAdmin()) {
            deletePost(postNo);
            return;
        }

        validateUserCanAccessPublicCommunity();
        validateUserOwnsWritablePost(post);
        communityPostRepository.delete(post);
    }

    private CommunityPostEntity findPost(Long postNo) {
        return communityPostRepository.findById(postNo)
                .orElseThrow(() -> new NotFoundDataException("게시글을 찾을 수 없습니다."));
    }

    CommunityPostEntity getVisiblePostEntity(Long postNo) {
        CommunityPostEntity post = findPost(postNo);
        if (!canReadCommunity()) {
            throw new AccessDeniedException("게시글을 볼 권한이 없습니다.");
        }
        return post;
    }

    private void validateUserCanWriteCommunity(CommunityPostCategory category) {
        validateUserCanAccessPublicCommunity();
        if (category != CommunityPostCategory.RECRUIT && category != CommunityPostCategory.CLAN_PROMOTION) {
            throw new UnprocessableContentException(
                    "INVALID_COMMUNITY_CATEGORY",
                    "일반 사용자는 내전모집 또는 클랜홍보 글만 작성할 수 있습니다."
            );
        }
    }

    private void validateUserOwnsWritablePost(CommunityPostEntity post) {
        String currentUserId = SecurityUtils.getCurrentUserIdFromAuthentication();
        boolean writableCategory = post.getCategory() == CommunityPostCategory.RECRUIT
                || post.getCategory() == CommunityPostCategory.CLAN_PROMOTION;
        if (!writableCategory || !currentUserId.equals(post.getWriterId())) {
            throw new AccessDeniedException("본인이 작성한 내전모집 또는 클랜홍보 글만 변경할 수 있습니다.");
        }
    }

    private CommunityPostResponseDTO saveNewPost(CommunityPostRequestDTO request) {
        CommunityPostEntity post = new CommunityPostEntity();
        post.setCategory(request.getCategory());
        post.setTitle(request.getTitle().trim());
        post.setContent(request.getContent().trim());
        post.setImageUrl(null);
        post.setWriterId(SecurityUtils.getCurrentUserIdFromAuthentication());
        post.setViewCount(0);

        return new CommunityPostResponseDTO(communityPostRepository.save(post));
    }

    private void validateAdminCanManageCommunity() {
        if (!SecurityUtils.isCurrentUserAdmin()) {
            throw new AccessDeniedException("관리자 권한이 필요합니다.");
        }
        if (!SecurityUtils.isCurrentUserOperator() && !communitySettingService.isVisibleToUsers()) {
            throw new AccessDeniedException("비공개 커뮤니티는 운영자만 변경할 수 있습니다.");
        }
    }

    private void validateNoticePermission(CommunityPostCategory category) {
        if (category == CommunityPostCategory.NOTICE && !SecurityUtils.isCurrentUserOperator()) {
            throw new AccessDeniedException("공지사항 작성과 수정은 운영자만 할 수 있습니다.");
        }
    }

    private void validateCanManageExistingNotice(CommunityPostEntity post) {
        if (post.getCategory() == CommunityPostCategory.NOTICE && !SecurityUtils.isCurrentUserOperator()) {
            throw new AccessDeniedException("공지사항 수정과 삭제는 운영자만 할 수 있습니다.");
        }
    }

    private void validateUserCanAccessPublicCommunity() {
        if (!communitySettingService.isVisibleToUsers()) {
            throw new AccessDeniedException("커뮤니티가 아직 공개되지 않았습니다.");
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
                .toList();
    }

    private boolean canReadCommunity() {
        return SecurityUtils.isCurrentUserAdmin() || communitySettingService.isVisibleToUsers();
    }
}
