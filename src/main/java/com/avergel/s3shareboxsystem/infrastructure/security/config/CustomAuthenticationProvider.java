package com.avergel.s3shareboxsystem.infrastructure.security.config;

import com.avergel.s3shareboxsystem.domain.auth.AuthenticationService;
import com.avergel.s3shareboxsystem.infrastructure.security.model.SpringSecurityUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final AuthenticationService authenticationService;

    public CustomAuthenticationProvider(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        SpringSecurityUser user = authenticationService.login(username, password);
        Map<String, Object> authenticatedCredentials = new HashMap<>();
        authenticatedCredentials.put("accessToken", user.getAccessToken());
        authenticatedCredentials.put("expiresIn", user.getExpiresIn());
        authenticatedCredentials.put("idToken", user.getIdToken());
        authenticatedCredentials.put("refreshToken", user.getRefreshToken());
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, authenticatedCredentials, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

        return usernamePasswordAuthenticationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
