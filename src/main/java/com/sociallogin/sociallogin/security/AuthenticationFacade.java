package com.sociallogin.sociallogin.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacade {
    public MyUserDetails getLoginUser() {
        return (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public int getLoginUserPk() {
        return getLoginUser().getMyPrincipal().getIuser();
    }
}
