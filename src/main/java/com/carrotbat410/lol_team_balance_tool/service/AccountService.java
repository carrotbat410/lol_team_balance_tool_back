package com.carrotbat410.lol_team_balance_tool.service;

import com.carrotbat410.lol_team_balance_tool.dto.DeleteAccountRequestDTO;
import com.carrotbat410.lol_team_balance_tool.entity.UserEntity;
import com.carrotbat410.lol_team_balance_tool.exHandler.exception.NotFoundDataException;
import com.carrotbat410.lol_team_balance_tool.exHandler.exception.UnprocessableContentException;
import com.carrotbat410.lol_team_balance_tool.repository.SummonerRepository;
import com.carrotbat410.lol_team_balance_tool.repository.UserRepository;
import com.carrotbat410.lol_team_balance_tool.repository.VisitorLogRepository;
import com.carrotbat410.lol_team_balance_tool.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private static final String ADMIN_ROLE = "ROLE_ADMIN";

    private final UserRepository userRepository;
    private final SummonerRepository summonerRepository;
    private final VisitorLogRepository visitorLogRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void deleteMyAccount(DeleteAccountRequestDTO request) {
        String userId = SecurityUtils.getCurrentUserIdFromAuthentication();
        UserEntity user = userRepository.findByUserId(userId);

        if (user == null) {
            throw new NotFoundDataException("사용자를 찾을 수 없습니다.", "USER_NOT_FOUND");
        }

        if (ADMIN_ROLE.equals(user.getRole())) {
            throw new UnprocessableContentException("ADMIN_ACCOUNT_DELETE_NOT_ALLOWED", "관리자 계정은 탈퇴할 수 없습니다.");
        }

        if (!bCryptPasswordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnprocessableContentException("PASSWORD_MISMATCH", "비밀번호가 일치하지 않습니다.");
        }

        summonerRepository.deleteByUserId(userId);
        visitorLogRepository.anonymizeUserId(userId);
        userRepository.delete(user);
    }
}
