package com.carrotbat410.lol_team_balance_tool.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;


//TODO 1. BaseEntity 상속받기
//TODO 2. NoArgsConstructor(access = AccessLevel.PROTECTED) 사용?
@Entity
@Getter
@Setter
public class UserEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String username;

    private String password;

    private String role;
}
