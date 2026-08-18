package com.carrotbat410.lol_team_balance_tool.utils;

import com.carrotbat410.lol_team_balance_tool.dto.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static final String ROLE_OPERATOR = "ROLE_OPERATOR";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";

    public static String getCurrentUserIdFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUserId();
    }

    public static String getCurrentUserIdOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getUserId();
        }

        return null;
    }

    public static boolean isCurrentUserAdmin() {
        return hasCurrentUserAnyRole(ROLE_OPERATOR, ROLE_ADMIN);
    }

    public static boolean isCurrentUserOperator() {
        return hasCurrentUserAnyRole(ROLE_OPERATOR);
    }

    public static boolean hasCurrentUserAnyRole(String... roles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> java.util.Arrays.asList(roles).contains(authority));
    }

}
