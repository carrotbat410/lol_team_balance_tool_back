package com.carrotbat410.lol_team_balance_tool.repository;

import com.carrotbat410.lol_team_balance_tool.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

}
