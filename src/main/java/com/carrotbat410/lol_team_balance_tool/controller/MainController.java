package com.carrotbat410.lol_team_balance_tool.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Iterator;


@RestController
public class MainController {

    @GetMapping("/")
    public String main() {
        //get username하는 방법
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        //get authority하는 방법
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();


        return "Main Controller : "+name + " | role : " + role;
    }
}
