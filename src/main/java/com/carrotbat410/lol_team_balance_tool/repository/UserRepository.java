package com.carrotbat410.lol_team_balance_tool.repository;

import com.carrotbat410.lol_team_balance_tool.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;
import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    boolean existsByUserId(String userId);

    UserEntity findByUserId(String userId); // 로그인할떄, CustomUserDetailsService클래스에서 사용할 method

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<UserEntity> findAllByRole(String role);
}
