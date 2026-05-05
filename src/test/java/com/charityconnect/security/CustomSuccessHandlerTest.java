package com.charityconnect.security;

import com.charityconnect.model.User;
import com.charityconnect.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;

class CustomSuccessHandlerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CustomSuccessHandler successHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldRedirectToInterestsWhenUserHasNoInterests() throws Exception {
        when(authentication.getAuthorities()).thenReturn((List) List.of(new SimpleGrantedAuthority("ROLE_USER")));
        when(authentication.getName()).thenReturn("newuser@test.com");
        
        User user = User.builder().email("newuser@test.com").interests(Collections.emptySet()).build();
        when(userRepository.findByEmail("newuser@test.com")).thenReturn(Optional.of(user));

        successHandler.onAuthenticationSuccess(request, response, authentication);

        verify(response).sendRedirect("/user/interests");
    }

    @Test
    void shouldRedirectToHomeWhenUserHasInterests() throws Exception {
        when(authentication.getAuthorities()).thenReturn((List) List.of(new SimpleGrantedAuthority("ROLE_USER")));
        when(authentication.getName()).thenReturn("olduser@test.com");
        
        User user = User.builder().email("olduser@test.com").interests(Set.of("Education")).build();
        when(userRepository.findByEmail("olduser@test.com")).thenReturn(Optional.of(user));

        successHandler.onAuthenticationSuccess(request, response, authentication);

        verify(response).sendRedirect("/");
    }

    @Test
    void shouldRedirectToAdminDashboardWhenAdminLogsIn() throws Exception {
        when(authentication.getAuthorities()).thenReturn((List) List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        successHandler.onAuthenticationSuccess(request, response, authentication);

        verify(response).sendRedirect("/admin/dashboard");
    }

    @Test
    void shouldRedirectToOrgDashboardWhenOrgLogsIn() throws Exception {
        when(authentication.getAuthorities()).thenReturn((List) List.of(new SimpleGrantedAuthority("ROLE_ORGANIZATION")));

        successHandler.onAuthenticationSuccess(request, response, authentication);

        verify(response).sendRedirect("/organization/dashboard");
    }
}
