package com.carrotbat410.lol_team_balance_tool.dto.response;

import com.carrotbat410.lol_team_balance_tool.entity.CommunityCommentEntity;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommunityCommentResponseDTO {

    private final Long no;
    private final Long postNo;
    private final String content;
    private final String writerId;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public CommunityCommentResponseDTO(
            Long no,
            Long postNo,
            String content,
            String writerId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.no = no;
        this.postNo = postNo;
        this.content = content;
        this.writerId = writerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public CommunityCommentResponseDTO(CommunityCommentEntity comment) {
        this.no = comment.getNo();
        this.postNo = comment.getPostNo();
        this.content = comment.getContent();
        this.writerId = comment.getWriterId();
        this.createdAt = comment.getCreated_at();
        this.updatedAt = comment.getUpdated_at();
    }
}
