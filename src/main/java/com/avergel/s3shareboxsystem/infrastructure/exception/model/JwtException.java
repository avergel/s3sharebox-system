package com.avergel.s3shareboxsystem.infrastructure.exception.model;

import org.springframework.security.core.AuthenticationException;

public class JwtException extends AuthenticationException {
    public JwtException(String msg) {
        super(msg);
    }
}
