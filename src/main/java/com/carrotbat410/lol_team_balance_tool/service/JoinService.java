package com.carrotbat410.lol_team_balance_tool.service;

import com.carrotbat410.lol_team_balance_tool.dto.JoinDTO;
import com.carrotbat410.lol_team_balance_tool.entity.UserEntity;
import com.carrotbat410.lol_team_balance_tool.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JoinService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public String joinProcess(JoinDTO joinDTO) {

        //TODO 이미 동일한 username이 존재하는지 판단하는 로직 추가하기

        boolean isUser = userRepository.existsByUsername(joinDTO.getUsername());
        if (isUser) {
            return "no"; //TODO 이부분 제대로 처리하기
        }

        UserEntity data = new UserEntity();

        data.setUsername(joinDTO.getUsername());
        data.setPassword(bCryptPasswordEncoder.encode(joinDTO.getPassword()));
        data.setRole("ROLE_USER");

        userRepository.save(data);
        return "yes";
    }


}
