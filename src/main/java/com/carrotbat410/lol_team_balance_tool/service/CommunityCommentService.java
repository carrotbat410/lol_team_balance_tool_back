package com.carrotbat410.lol_team_balance_tool.service;

import com.carrotbat410.lol_team_balance_tool.dto.CommunityCommentRequestDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.CommunityCommentResponseDTO;
import com.carrotbat410.lol_team_balance_tool.entity.CommunityCommentEntity;
import com.carrotbat410.lol_team_balance_tool.entity.CommunityPostEntity;
import com.carrotbat410.lol_team_balance_tool.exHandler.exception.NotFoundDataException;
import com.carrotbat410.lol_team_balance_tool.repository.CommunityCommentRepository;
import com.carrotbat410.lol_team_balance_tool.repository.CommunityPostRepository;
import com.carrotbat410.lol_team_balance_tool.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityCommentService {

    private final CommunityCommentRepository communityCommentRepository;
    private final CommunityPostRepository communityPostRepository;
    private final CommunitySettingService communitySettingService;

    public List<CommunityCommentResponseDTO> getComments(Long postNo) {
        CommunityPostEntity post = findPost(postNo);
        validateCanReadPost(post);

        return communityCommentRepository.findByPostNoOrderByNoAsc(postNo);
    }

    @Transactional
    public CommunityCommentResponseDTO createComment(Long postNo, CommunityCommentRequestDTO request) {
        findPost(postNo);
        validateCanWriteCommunity();

        CommunityCommentEntity comment = new CommunityCommentEntity();
        comment.setPostNo(postNo);
        comment.setContent(request.getContent().trim());
        comment.setWriterId(SecurityUtils.getCurrentUserIdFromAuthentication());

        return new CommunityCommentResponseDTO(communityCommentRepository.save(comment));
    }

    @Transactional
    public CommunityCommentResponseDTO updateComment(Long postNo, Long commentNo, CommunityCommentRequestDTO request) {
        findPost(postNo);
        validateCanWriteCommunity();

        CommunityCommentEntity comment = findComment(commentNo);
        validateCommentBelongsToPost(comment, postNo);
        validateCanManageComment(comment);

        comment.setContent(request.getContent().trim());

        return new CommunityCommentResponseDTO(communityCommentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(Long postNo, Long commentNo) {
        findPost(postNo);
        validateCanWriteCommunity();

        CommunityCommentEntity comment = findComment(commentNo);
        validateCommentBelongsToPost(comment, postNo);
        validateCanManageComment(comment);

        communityCommentRepository.delete(comment);
    }

    private CommunityPostEntity findPost(Long postNo) {
        return communityPostRepository.findById(postNo)
                .orElseThrow(() -> new NotFoundDataException("게시글을 찾을 수 없습니다."));
    }

    private CommunityCommentEntity findComment(Long commentNo) {
        return communityCommentRepository.findById(commentNo)
                .orElseThrow(() -> new NotFoundDataException("댓글을 찾을 수 없습니다."));
    }

    private void validateCanReadPost(CommunityPostEntity post) {
        if (SecurityUtils.isCurrentUserAdmin() || communitySettingService.isVisibleToUsers()) {
            return;
        }

        throw new AccessDeniedException("게시글을 볼 권한이 없습니다.");
    }

    private void validateCanWriteCommunity() {
        if (SecurityUtils.isCurrentUserOperator() || communitySettingService.isVisibleToUsers()) {
            return;
        }

        throw new AccessDeniedException("비공개 커뮤니티는 운영자만 변경할 수 있습니다.");
    }

    private void validateCommentBelongsToPost(CommunityCommentEntity comment, Long postNo) {
        if (!postNo.equals(comment.getPostNo())) {
            throw new NotFoundDataException("댓글을 찾을 수 없습니다.");
        }
    }

    private void validateCanManageComment(CommunityCommentEntity comment) {
        if (SecurityUtils.isCurrentUserAdmin()) {
            return;
        }

        String currentUserId = SecurityUtils.getCurrentUserIdFromAuthentication();
        if (!currentUserId.equals(comment.getWriterId())) {
            throw new AccessDeniedException("본인이 작성한 댓글만 변경할 수 있습니다.");
        }
    }
}
