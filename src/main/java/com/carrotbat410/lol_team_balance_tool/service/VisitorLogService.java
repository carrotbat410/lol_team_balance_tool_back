package com.carrotbat410.lol_team_balance_tool.service;

import com.carrotbat410.lol_team_balance_tool.dto.CustomUserDetails;
import com.carrotbat410.lol_team_balance_tool.dto.VisitTrackRequestDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.DailyVisitStatsDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.LoggedInVisitorStatsDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.VisitSummaryResponseDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.VisitTrackResponseDTO;
import com.carrotbat410.lol_team_balance_tool.entity.VisitorLogEntity;
import com.carrotbat410.lol_team_balance_tool.repository.VisitorLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class VisitorLogService {

    private static final int MAX_STATS_DAYS = 90;

    private final VisitorLogRepository visitorLogRepository;

    @Transactional
    public VisitTrackResponseDTO trackVisit(VisitTrackRequestDTO request) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        String userId = getCurrentUserIdOrNull();
        String visitorId = normalizeVisitorId(request.getVisitorId());
        String visitorKey = userId == null ? "guest:" + visitorId : "user:" + userId;

        Optional<VisitorLogEntity> existingLog = visitorLogRepository.findByVisitDateAndVisitorKey(today, visitorKey);
        if (existingLog.isPresent()) {
            return updateExistingLog(existingLog.get(), now);
        }

        Optional<VisitorLogEntity> guestLog = findGuestLogForSignedInVisitor(today, visitorId, userId);
        if (guestLog.isPresent()) {
            return updateExistingLogAsSignedIn(guestLog.get(), now, userId, visitorKey);
        }

        return createNewLog(request, today, now, visitorId, userId, visitorKey);
    }

    @Transactional(readOnly = true)
    public VisitSummaryResponseDTO getVisitSummary(int requestedDays) {
        int days = Math.min(Math.max(requestedDays, 1), MAX_STATS_DAYS);
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1L);
        List<VisitorLogEntity> logs = visitorLogRepository.findByVisitDateBetweenOrderByVisitDateAsc(startDate, endDate);

        Map<LocalDate, MutableDailyVisitStats> dailyStatsMap = new LinkedHashMap<>();
        for (int i = 0; i < days; i++) {
            dailyStatsMap.put(startDate.plusDays(i), new MutableDailyVisitStats());
        }

        Set<String> totalUniqueVisitors = new HashSet<>();
        Set<String> loggedInUniqueVisitors = new HashSet<>();
        Set<String> guestUniqueVisitors = new HashSet<>();
        Map<String, MutableLoggedInVisitorStats> loggedInVisitorStatsMap = new LinkedHashMap<>();

        for (VisitorLogEntity log : logs) {
            MutableDailyVisitStats dailyStats = dailyStatsMap.get(log.getVisitDate());
            if (dailyStats == null) {
                continue;
            }

            totalUniqueVisitors.add(log.getVisitorKey());
            dailyStats.totalVisitors++;

            if (log.getUserId() == null || log.getUserId().isBlank()) {
                guestUniqueVisitors.add(log.getVisitorKey());
                dailyStats.guestVisitors++;
            } else {
                loggedInUniqueVisitors.add(log.getVisitorKey());
                dailyStats.loggedInVisitors++;
                loggedInVisitorStatsMap
                        .computeIfAbsent(log.getUserId(), MutableLoggedInVisitorStats::new)
                        .add(log);
            }
        }

        List<DailyVisitStatsDTO> dailyStats = new ArrayList<>();
        long totalDailyVisitors = 0;
        for (Map.Entry<LocalDate, MutableDailyVisitStats> entry : dailyStatsMap.entrySet()) {
            MutableDailyVisitStats stats = entry.getValue();
            totalDailyVisitors += stats.totalVisitors;
            dailyStats.add(new DailyVisitStatsDTO(
                    entry.getKey(),
                    stats.totalVisitors,
                    stats.loggedInVisitors,
                    stats.guestVisitors
            ));
        }

        return new VisitSummaryResponseDTO(
                days,
                startDate,
                endDate,
                totalUniqueVisitors.size(),
                loggedInUniqueVisitors.size(),
                guestUniqueVisitors.size(),
                totalDailyVisitors,
                dailyStats,
                toLoggedInVisitorStats(loggedInVisitorStatsMap)
        );
    }

    private List<LoggedInVisitorStatsDTO> toLoggedInVisitorStats(
            Map<String, MutableLoggedInVisitorStats> loggedInVisitorStatsMap
    ) {
        return loggedInVisitorStatsMap.values().stream()
                .sorted(
                        Comparator
                                .comparingLong(MutableLoggedInVisitorStats::getVisitDays).reversed()
                                .thenComparing(MutableLoggedInVisitorStats::getLastVisitedAt, Comparator.reverseOrder())
                                .thenComparing(MutableLoggedInVisitorStats::getUserId)
                )
                .map((stats) -> new LoggedInVisitorStatsDTO(
                        stats.userId,
                        stats.getVisitDays(),
                        stats.hitCount,
                        stats.firstVisitDate,
                        stats.lastVisitDate,
                        stats.lastVisitedAt
                ))
                .toList();
    }

    private VisitTrackResponseDTO updateExistingLog(VisitorLogEntity existingLog, LocalDateTime now) {
        existingLog.setLastVisitedAt(now);
        existingLog.setHitCount(existingLog.getHitCount() + 1);
        return new VisitTrackResponseDTO(existingLog.getVisitDate(), false);
    }

    private Optional<VisitorLogEntity> findGuestLogForSignedInVisitor(
            LocalDate today,
            String visitorId,
            String userId
    ) {
        if (userId == null) {
            return Optional.empty();
        }

        return visitorLogRepository.findByVisitDateAndVisitorKey(today, "guest:" + visitorId);
    }

    private VisitTrackResponseDTO updateExistingLogAsSignedIn(
            VisitorLogEntity existingLog,
            LocalDateTime now,
            String userId,
            String visitorKey
    ) {
        existingLog.setUserId(userId);
        existingLog.setVisitorKey(visitorKey);
        existingLog.setLastVisitedAt(now);
        existingLog.setHitCount(existingLog.getHitCount() + 1);
        return new VisitTrackResponseDTO(existingLog.getVisitDate(), false);
    }

    private VisitTrackResponseDTO createNewLog(
            VisitTrackRequestDTO request,
            LocalDate today,
            LocalDateTime now,
            String visitorId,
            String userId,
            String visitorKey
    ) {
        VisitorLogEntity visitorLog = new VisitorLogEntity();
        visitorLog.setVisitDate(today);
        visitorLog.setVisitorKey(visitorKey);
        visitorLog.setVisitorId(visitorId);
        visitorLog.setUserId(userId);
        visitorLog.setFirstPath(request.getPath());
        visitorLog.setFirstVisitedAt(now);
        visitorLog.setLastVisitedAt(now);
        visitorLog.setHitCount(1);
        visitorLogRepository.save(visitorLog);

        return new VisitTrackResponseDTO(today, true);
    }

    private String getCurrentUserIdOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getUserId();
        }

        return null;
    }

    private String normalizeVisitorId(String visitorId) {
        return visitorId == null ? "" : visitorId.replaceAll("[^a-zA-Z0-9_-]", "").trim();
    }

    private static class MutableDailyVisitStats {
        private long totalVisitors;
        private long loggedInVisitors;
        private long guestVisitors;
    }

    private static class MutableLoggedInVisitorStats {
        private final String userId;
        private final Set<LocalDate> visitDates = new HashSet<>();
        private long hitCount;
        private LocalDate firstVisitDate;
        private LocalDate lastVisitDate;
        private LocalDateTime lastVisitedAt;

        private MutableLoggedInVisitorStats(String userId) {
            this.userId = userId;
        }

        private void add(VisitorLogEntity log) {
            visitDates.add(log.getVisitDate());
            hitCount += log.getHitCount();

            if (firstVisitDate == null || log.getVisitDate().isBefore(firstVisitDate)) {
                firstVisitDate = log.getVisitDate();
            }

            if (lastVisitDate == null || log.getVisitDate().isAfter(lastVisitDate)) {
                lastVisitDate = log.getVisitDate();
            }

            if (lastVisitedAt == null || log.getLastVisitedAt().isAfter(lastVisitedAt)) {
                lastVisitedAt = log.getLastVisitedAt();
            }
        }

        private String getUserId() {
            return userId;
        }

        private long getVisitDays() {
            return visitDates.size();
        }

        private LocalDateTime getLastVisitedAt() {
            return lastVisitedAt;
        }
    }
}
