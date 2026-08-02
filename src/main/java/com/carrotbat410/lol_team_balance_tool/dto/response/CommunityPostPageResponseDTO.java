package com.carrotbat410.lol_team_balance_tool.dto.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class CommunityPostPageResponseDTO {

    private final List<CommunityPostResponseDTO> notices;
    private final List<CommunityPostResponseDTO> posts;
    private final int page;
    private final int size;
    private final int totalPages;
    private final long totalElements;

    public CommunityPostPageResponseDTO(
            List<CommunityPostResponseDTO> notices,
            Page<CommunityPostResponseDTO> posts
    ) {
        this.notices = notices;
        this.posts = posts.getContent();
        this.page = posts.getNumber();
        this.size = posts.getSize();
        this.totalPages = posts.getTotalPages();
        this.totalElements = posts.getTotalElements();
    }
}
