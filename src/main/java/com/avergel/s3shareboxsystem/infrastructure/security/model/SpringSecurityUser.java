package com.avergel.s3shareboxsystem.infrastructure.security.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SpringSecurityUser {
    private String username;
    private String accessToken;
    private Integer expiresIn;
    private String tokenType;
    private String refreshToken;
    private String idToken;
}
