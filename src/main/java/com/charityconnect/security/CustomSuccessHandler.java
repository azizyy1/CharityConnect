package com.charityconnect.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String redirectUrl = "/";

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            if ("ROLE_ADMIN".equals(role)) {
                redirectUrl = "/admin/dashboard";
                break;
            }
            if ("ROLE_ORGANIZATION".equals(role)) {
                redirectUrl = "/organization/dashboard";
                break;
            }
            if ("ROLE_USER".equals(role)) {
                redirectUrl = "/user/dashboard";
                break;
            }
        }

        response.sendRedirect(redirectUrl);
    }
}
