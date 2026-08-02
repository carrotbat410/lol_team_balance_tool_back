package com.carrotbat410.lol_team_balance_tool.controller;

import com.carrotbat410.lol_team_balance_tool.dto.CommunityCommentRequestDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.CommunityCommentResponseDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.SuccessResponseDTO;
import com.carrotbat410.lol_team_balance_tool.service.CommunityCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community/posts/{postNo}/comments")
@Tag(name = "CommunityCommentController", description = "커뮤니티 댓글 컨트롤러")
public class CommunityCommentController {

    private final CommunityCommentService communityCommentService;

    @GetMapping
    @Operation(summary = "커뮤니티 댓글 목록", description = "게시글의 댓글 목록을 조회합니다.")
    public SuccessResponseDTO<List<CommunityCommentResponseDTO>> comments(@PathVariable Long postNo) {
        return new SuccessResponseDTO<>("community comments", communityCommentService.getComments(postNo));
    }

    @PostMapping
    @Operation(summary = "커뮤니티 댓글 작성", description = "로그인 사용자가 댓글을 작성합니다.")
    public SuccessResponseDTO<CommunityCommentResponseDTO> createComment(
            @PathVariable Long postNo,
            @Valid @RequestBody CommunityCommentRequestDTO request
    ) {
        return new SuccessResponseDTO<>("community comment created", communityCommentService.createComment(postNo, request));
    }

    @PatchMapping("/{commentNo}")
    @Operation(summary = "커뮤니티 댓글 수정", description = "본인이 작성한 댓글 또는 관리자가 댓글을 수정합니다.")
    public SuccessResponseDTO<CommunityCommentResponseDTO> updateComment(
            @PathVariable Long postNo,
            @PathVariable Long commentNo,
            @Valid @RequestBody CommunityCommentRequestDTO request
    ) {
        return new SuccessResponseDTO<>("community comment updated", communityCommentService.updateComment(postNo, commentNo, request));
    }

    @DeleteMapping("/{commentNo}")
    @Operation(summary = "커뮤니티 댓글 삭제", description = "본인이 작성한 댓글 또는 관리자가 댓글을 삭제합니다.")
    public SuccessResponseDTO<Void> deleteComment(
            @PathVariable Long postNo,
            @PathVariable Long commentNo
    ) {
        communityCommentService.deleteComment(postNo, commentNo);
        return new SuccessResponseDTO<>("community comment deleted");
    }
}
