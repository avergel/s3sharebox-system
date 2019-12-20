package com.avergel.s3shareboxsystem.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class AuthenticationRequest {
    private String username;
    private String password;
}
