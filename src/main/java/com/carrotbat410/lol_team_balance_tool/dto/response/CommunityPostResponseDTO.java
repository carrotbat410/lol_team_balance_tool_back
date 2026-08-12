package com.carrotbat410.lol_team_balance_tool.dto.response;

import com.carrotbat410.lol_team_balance_tool.entity.CommunityPostCategory;
import com.carrotbat410.lol_team_balance_tool.entity.CommunityPostEntity;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommunityPostResponseDTO {

    private final Long no;
    private final CommunityPostCategory category;
    private final String title;
    private final String content;
    private final String writerId;
    private final long viewCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public CommunityPostResponseDTO(
            Long no,
            CommunityPostCategory category,
            String title,
            String content,
            String writerId,
            long viewCount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.no = no;
        this.category = category;
        this.title = title;
        this.content = content;
        this.writerId = writerId;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public CommunityPostResponseDTO(CommunityPostEntity post) {
        this.no = post.getNo();
        this.category = post.getCategory();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.writerId = post.getWriterId();
        this.viewCount = post.getViewCount();
        this.createdAt = post.getCreated_at();
        this.updatedAt = post.getUpdated_at();
    }
}
