package com.carrotbat410.lol_team_balance_tool.service;

import com.carrotbat410.lol_team_balance_tool.dto.ChangePasswordRequestDTO;
import com.carrotbat410.lol_team_balance_tool.dto.DeleteAccountRequestDTO;
import com.carrotbat410.lol_team_balance_tool.entity.CommunityPostEntity;
import com.carrotbat410.lol_team_balance_tool.entity.UserEntity;
import com.carrotbat410.lol_team_balance_tool.exHandler.exception.NotFoundDataException;
import com.carrotbat410.lol_team_balance_tool.exHandler.exception.UnprocessableContentException;
import com.carrotbat410.lol_team_balance_tool.repository.CommunityCommentRepository;
import com.carrotbat410.lol_team_balance_tool.repository.CommunityPostRepository;
import com.carrotbat410.lol_team_balance_tool.repository.SummonerRepository;
import com.carrotbat410.lol_team_balance_tool.repository.UserRepository;
import com.carrotbat410.lol_team_balance_tool.repository.VisitorLogRepository;
import com.carrotbat410.lol_team_balance_tool.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private static final int MIN_PASSWORD_CODE_POINTS = 6;
    private static final int MAX_PASSWORD_CODE_POINTS = 72;
    private static final int BCRYPT_MAX_PASSWORD_BYTES = 72;

    private final UserRepository userRepository;
    private final SummonerRepository summonerRepository;
    private final VisitorLogRepository visitorLogRepository;
    private final CommunityPostRepository communityPostRepository;
    private final CommunityCommentRepository communityCommentRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void changePassword(ChangePasswordRequestDTO request) {
        String userId = SecurityUtils.getCurrentUserIdFromAuthentication();
        UserEntity user = userRepository.findByUserId(userId);

        if (user == null) {
            throw new NotFoundDataException("사용자를 찾을 수 없습니다.", "USER_NOT_FOUND");
        }

        if (exceedsBcryptPasswordLimit(request.getCurrentPassword())
                || !bCryptPasswordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new UnprocessableContentException("CURRENT_PASSWORD_MISMATCH", "현재 비밀번호가 일치하지 않습니다.");
        }

        if (hasInvalidPasswordLength(request.getNewPassword())
                || hasInvalidPasswordLength(request.getNewPasswordConfirm())) {
            throw new UnprocessableContentException(
                    "PASSWORD_LENGTH_INVALID",
                    "새 비밀번호는 6자 이상 72자 이하로 입력해주세요."
            );
        }

        if (exceedsBcryptPasswordLimit(request.getNewPassword())
                || exceedsBcryptPasswordLimit(request.getNewPasswordConfirm())) {
            throw new UnprocessableContentException(
                    "PASSWORD_TOO_LONG",
                    "새 비밀번호는 UTF-8 기준 72바이트 이하로 입력해주세요."
            );
        }

        if (!request.getNewPassword().equals(request.getNewPasswordConfirm())) {
            throw new UnprocessableContentException("PASSWORD_CONFIRMATION_MISMATCH", "새 비밀번호와 새 비밀번호 확인이 일치하지 않습니다.");
        }

        if (bCryptPasswordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new UnprocessableContentException("NEW_PASSWORD_SAME_AS_CURRENT", "새 비밀번호는 현재 비밀번호와 달라야 합니다.");
        }

        user.setPassword(bCryptPasswordEncoder.encode(request.getNewPassword()));
    }

    private boolean hasInvalidPasswordLength(String password) {
        int codePointCount = password.codePointCount(0, password.length());
        return codePointCount < MIN_PASSWORD_CODE_POINTS || codePointCount > MAX_PASSWORD_CODE_POINTS;
    }

    private boolean exceedsBcryptPasswordLimit(String password) {
        return password.getBytes(StandardCharsets.UTF_8).length > BCRYPT_MAX_PASSWORD_BYTES;
    }

    @Transactional
    public void deleteMyAccount(DeleteAccountRequestDTO request) {
        String userId = SecurityUtils.getCurrentUserIdFromAuthentication();
        UserEntity user = userRepository.findByUserId(userId);

        if (user == null) {
            throw new NotFoundDataException("사용자를 찾을 수 없습니다.", "USER_NOT_FOUND");
        }

        if (SecurityUtils.ROLE_ADMIN.equals(user.getRole()) || SecurityUtils.ROLE_OPERATOR.equals(user.getRole())) {
            throw new UnprocessableContentException(
                    "PRIVILEGED_ACCOUNT_DELETE_NOT_ALLOWED",
                    "관리자 및 운영자 계정은 회원 탈퇴를 할 수 없습니다."
            );
        }

        if (exceedsBcryptPasswordLimit(request.getPassword())
                || !bCryptPasswordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnprocessableContentException("PASSWORD_MISMATCH", "비밀번호가 일치하지 않습니다.");
        }

        deleteCommunityActivity(userId);
        summonerRepository.deleteByUserId(userId);
        visitorLogRepository.anonymizeUserId(userId);
        userRepository.delete(user);
    }

    private void deleteCommunityActivity(String userId) {
        List<Long> postNos = communityPostRepository.findByWriterId(userId).stream()
                .map(CommunityPostEntity::getNo)
                .toList();

        if (!postNos.isEmpty()) {
            communityCommentRepository.deleteByPostNoIn(postNos);
        }

        communityCommentRepository.deleteByWriterId(userId);
        communityPostRepository.deleteByWriterId(userId);
    }
}
