package com.carrotbat410.lol_team_balance_tool.controller;

import com.carrotbat410.lol_team_balance_tool.dto.CommunityPostRequestDTO;
import com.carrotbat410.lol_team_balance_tool.dto.CommunityPostViewRequestDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.CommunityPostPageResponseDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.CommunityPostResponseDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.CommunityPostViewResponseDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.SuccessResponseDTO;
import com.carrotbat410.lol_team_balance_tool.entity.CommunityPostCategory;
import com.carrotbat410.lol_team_balance_tool.service.CommunityPostService;
import com.carrotbat410.lol_team_balance_tool.service.CommunityPostViewService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community/posts")
@Tag(name = "CommunityPostController", description = "커뮤니티 게시글 컨트롤러")
public class CommunityPostController {

    private final CommunityPostService communityPostService;
    private final CommunityPostViewService communityPostViewService;

    @GetMapping
    @Operation(summary = "커뮤니티 게시글 목록", description = "커뮤니티 게시글 목록을 조회합니다.")
    public SuccessResponseDTO<CommunityPostPageResponseDTO> posts(
            @RequestParam(defaultValue = "RECRUIT") CommunityPostCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return new SuccessResponseDTO<>("community posts", communityPostService.getVisiblePostPage(category, page, size));
    }

    @GetMapping("/{postNo}")
    @Operation(summary = "커뮤니티 게시글 상세", description = "커뮤니티 게시글 상세를 조회합니다.")
    public SuccessResponseDTO<CommunityPostResponseDTO> post(@PathVariable Long postNo) {
        return new SuccessResponseDTO<>("community post", communityPostService.getVisiblePost(postNo));
    }

    @PostMapping("/{postNo}/views")
    @Operation(summary = "커뮤니티 게시글 조회수 기록", description = "게시글의 일일 고유 조회수를 기록합니다.")
    public SuccessResponseDTO<CommunityPostViewResponseDTO> recordView(
            @PathVariable Long postNo,
            @Valid @RequestBody CommunityPostViewRequestDTO request
    ) {
        return new SuccessResponseDTO<>(
                "community post view recorded",
                communityPostViewService.recordView(postNo, request)
        );
    }

    @PostMapping
    @Operation(summary = "커뮤니티 게시글 작성", description = "로그인 사용자가 내전모집 게시글을 작성합니다.")
    public SuccessResponseDTO<CommunityPostResponseDTO> createPost(
            @Valid @RequestBody CommunityPostRequestDTO request
    ) {
        return new SuccessResponseDTO<>("community post created", communityPostService.createUserPost(request));
    }

    @PatchMapping("/{postNo}")
    @Operation(summary = "커뮤니티 게시글 수정", description = "본인이 작성한 내전모집 게시글을 수정합니다.")
    public SuccessResponseDTO<CommunityPostResponseDTO> updatePost(
            @PathVariable Long postNo,
            @Valid @RequestBody CommunityPostRequestDTO request
    ) {
        return new SuccessResponseDTO<>("community post updated", communityPostService.updateUserPost(postNo, request));
    }

    @DeleteMapping("/{postNo}")
    @Operation(summary = "커뮤니티 게시글 삭제", description = "본인이 작성한 내전모집 게시글을 삭제합니다.")
    public SuccessResponseDTO<Void> deletePost(@PathVariable Long postNo) {
        communityPostService.deleteUserPost(postNo);
        return new SuccessResponseDTO<>("community post deleted");
    }
}
