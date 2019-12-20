package com.avergel.s3shareboxsystem.domain.auth;

import com.avergel.s3shareboxsystem.infrastructure.security.model.SpringSecurityUser;
import org.springframework.security.core.Authentication;

import java.util.Map;

public interface AuthenticationService {
    Authentication getAuthentication(String jwtToken);

    SpringSecurityUser login(String user, String password);

    Map<String, Object> refreshToken(String refreshToken);
}
