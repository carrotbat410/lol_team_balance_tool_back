package com.carrotbat410.lol_team_balance_tool.service;

import com.carrotbat410.lol_team_balance_tool.dto.AdminUserRoleUpdateRequestDTO;
import com.carrotbat410.lol_team_balance_tool.dto.response.AdminUserResponseDTO;
import com.carrotbat410.lol_team_balance_tool.entity.UserEntity;
import com.carrotbat410.lol_team_balance_tool.exHandler.exception.NotFoundDataException;
import com.carrotbat410.lol_team_balance_tool.exHandler.exception.UnprocessableContentException;
import com.carrotbat410.lol_team_balance_tool.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private static final Set<String> ALLOWED_ROLES = Set.of("ROLE_USER", "ROLE_ADMIN");

    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    public List<AdminUserResponseDTO> getUsers() {
        String createdAtExpression = hasCreatedAtColumn() ? "created_at" : "NULL";
        String sql = """
                SELECT no, user_id, role, %s AS created_at
                FROM users
                ORDER BY no DESC
                """.formatted(createdAtExpression);

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Timestamp createdAt = rs.getTimestamp("created_at");
            LocalDateTime createdAtValue = createdAt == null ? null : createdAt.toLocalDateTime();

            return new AdminUserResponseDTO(
                    rs.getInt("no"),
                    rs.getString("user_id"),
                    rs.getString("role"),
                    createdAtValue
            );
        });
    }

    @Transactional
    public AdminUserResponseDTO updateRole(int userNo, AdminUserRoleUpdateRequestDTO request) {
        String normalizedRole = normalizeRole(request.getRole());
        if (!ALLOWED_ROLES.contains(normalizedRole)) {
            throw new UnprocessableContentException("INVALID_ROLE", "변경할 수 없는 권한입니다.");
        }

        UserEntity user = userRepository.findById(userNo)
                .orElseThrow(() -> new NotFoundDataException("회원을 찾을 수 없습니다."));
        user.setRole(normalizedRole);
        userRepository.saveAndFlush(user);

        return findUserResponse(userNo);
    }

    private AdminUserResponseDTO findUserResponse(int userNo) {
        String createdAtExpression = hasCreatedAtColumn() ? "created_at" : "NULL";
        String sql = """
                SELECT no, user_id, role, %s AS created_at
                FROM users
                WHERE no = ?
                """.formatted(createdAtExpression);

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Timestamp createdAt = rs.getTimestamp("created_at");
            LocalDateTime createdAtValue = createdAt == null ? null : createdAt.toLocalDateTime();

            return new AdminUserResponseDTO(
                    rs.getInt("no"),
                    rs.getString("user_id"),
                    rs.getString("role"),
                    createdAtValue
            );
        }, userNo);
    }

    private String normalizeRole(String role) {
        return role == null ? "" : role.trim().toUpperCase();
    }

    private boolean hasCreatedAtColumn() {
        Integer count = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM information_schema.columns
                WHERE table_schema = DATABASE()
                  AND table_name = 'users'
                  AND column_name = 'created_at'
                """, Integer.class);

        return count != null && count > 0;
    }
}
