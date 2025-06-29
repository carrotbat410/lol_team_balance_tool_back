package com.carrotbat410.lol_team_balance_tool.dto;

import com.carrotbat410.lol_team_balance_tool.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final UserEntity userEntity;

    public CustomUserDetails(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    //* INFO CustomUserDetails 클래스의 getAuthorities() 메서드에 의해 .requestMatchers("/admin").hasRole("ADMIN") 부분이 제대로 동작할 수 있습니다.
    //* 그 이유는 Spring Security가 사용자의 권한을 GrantedAuthority 객체를 통해 관리하고, 이를 Authentication 객체에 저장하여 권한을 검사하기 때문입니다.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return userEntity.getRole();
            }
        });

        return collection;
    }

    @Override
    public String getPassword() {
        return userEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return userEntity.getUserId();
    }

    public int getNo() {
        return userEntity.getNo();
    }


    //* INFO 아래 부분들은 일단 true로 패스하도록 설정함. 추후에 필요하다면 아래 부분들 검증하는 로직을 userEntity에 필드값들을 추가해서 구현하면됨.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
