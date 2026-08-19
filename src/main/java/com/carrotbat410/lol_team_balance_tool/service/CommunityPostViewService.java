package com.carrotbat410.lol_team_balance_tool.service;

import com.carrotbat410.lol_team_balance_tool.dto.CommunityPostViewRequestDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.CommunityPostViewResponseDTO;
import com.carrotbat410.lol_team_balance_tool.exHandler.exception.NotFoundDataException;
import com.carrotbat410.lol_team_balance_tool.repository.CommunityPostRepository;
import com.carrotbat410.lol_team_balance_tool.repository.CommunityPostViewRepository;
import com.carrotbat410.lol_team_balance_tool.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class CommunityPostViewService {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    private final CommunityPostService communityPostService;
    private final CommunityPostRepository communityPostRepository;
    private final CommunityPostViewRepository communityPostViewRepository;

    @Transactional
    public CommunityPostViewResponseDTO recordView(Long postNo, CommunityPostViewRequestDTO request) {
        communityPostService.getVisiblePostEntity(postNo);
        long lockedViewCount = communityPostRepository.findViewCountByNoForUpdate(postNo)
                .orElseThrow(() -> new NotFoundDataException("게시글을 찾을 수 없습니다."));

        String userId = SecurityUtils.getCurrentUserIdOrNull();
        String viewerKey = userId == null
                ? "guest:" + request.getVisitorId()
                : "user:" + userId;
        LocalDateTime now = LocalDateTime.now(SEOUL_ZONE);
        int inserted = communityPostViewRepository.insertIgnore(
                postNo,
                sha256(viewerKey),
                now.toLocalDate(),
                now
        );

        boolean counted = inserted == 1;
        if (counted && communityPostRepository.incrementViewCount(postNo) != 1) {
            throw new NotFoundDataException("게시글을 찾을 수 없습니다.");
        }

        return new CommunityPostViewResponseDTO(counted, lockedViewCount + (counted ? 1 : 0));
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }
}
