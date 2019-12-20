package com.avergel.s3shareboxsystem.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
@JsonInclude(NON_NULL)
public class AuthenticationResponse {
    private String username;
    private String idToken;
    private String accessToken;
    private String refreshToken;
    private Integer expiresIn;
}
