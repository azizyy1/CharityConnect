package com.charityconnect.security;

import com.charityconnect.model.User;
import com.charityconnect.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

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
                Optional<User> userOpt = userRepository.findByEmail(authentication.getName());
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    if (user.getInterests() == null || user.getInterests().isEmpty()) {
                        redirectUrl = "/user/interests";
                    } else {
                        redirectUrl = "/";
                    }
                } else {
                    redirectUrl = "/";
                }
                break;
            }
        }

        response.sendRedirect(redirectUrl);
    }
}
