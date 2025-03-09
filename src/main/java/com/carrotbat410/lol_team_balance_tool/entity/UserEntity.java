package com.carrotbat410.lol_team_balance_tool.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;


//TODO 1. BaseEntity 상속받기
//TODO 2. NoArgsConstructor(access = AccessLevel.PROTECTED) 사용?
@Entity
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;

    private String password;

    private String role;
}
