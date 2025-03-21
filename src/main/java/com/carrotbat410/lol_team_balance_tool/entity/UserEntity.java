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
    //TODO 아래에 대해서 공부하고 포스팅하기
    //0. Spring Security + JWT 방식일떄는 직렬화/역직렬화 사용할 필요 없었음 (UserEntity, CustomUserDetails 클래스에다가 Serializable 구현해줘야했음)
    //1. 직렬화/역직렬화,  객체를 직렬화하면 객체의 상태를 바이트 스트림으로 변환하여 저장하거나 네트워크를 통해 전송할 수 있다.
    //2. serialVersionUID 필드 없어도 되던데 (implements Serializable만 사용해도 직렬화는 가능, 그러나 역직렬화하려면 serialVersionUID가 동일해야하며 명시하는 것이 좋은 습관이다.??)
    //3. 왜 1L로 값을 고정하는거지?
    //4. serialVersionUID를 명시하지 않으면, Java는 클래스 구조를 기반으로 자동으로 이 값을 생성한다?? (그러나 이 자동 생성 방식은 클래스 구조가 조금만 변경돼도 serialVersionUID 값이 달라져 호환성 문제가 발생할 수 있어 안전하지 않다?)


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
