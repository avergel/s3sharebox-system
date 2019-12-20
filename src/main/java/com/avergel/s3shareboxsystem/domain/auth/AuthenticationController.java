package com.avergel.s3shareboxsystem.domain.auth;

import com.avergel.s3shareboxsystem.domain.auth.dto.AuthenticationRequest;
import com.avergel.s3shareboxsystem.domain.auth.dto.AuthenticationResponse;
import com.avergel.s3shareboxsystem.domain.auth.dto.RefreshTokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.avergel.s3shareboxsystem.infrastructure.util.Constants.*;
import static lombok.AccessLevel.PACKAGE;

@CrossOrigin(origins = {"${frontendServer}"})
@RestController
@RequestMapping("auth")
@RequiredArgsConstructor(access = PACKAGE)
class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final AuthenticationService authenticationService;

    @PostMapping("login")
    ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest authenticationRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        Map<String, Object> authenticatedCredentials = (Map<String, Object>) authentication.getCredentials();
        String accessToken = (String) authenticatedCredentials.get(ACCESS_TOKEN);
        String idToken = (String) authenticatedCredentials.get(ID_TOKEN);
        String refreshToken = (String) authenticatedCredentials.get(REFRESH_TOKEN);
        Integer expiresIn = (Integer) authenticatedCredentials.get(EXPIRES_IN);

        return ResponseEntity.ok(AuthenticationResponse.builder()
                                                       .username(authenticationRequest.getUsername())
                                                       .idToken(idToken)
                                                       .accessToken(accessToken)
                                                       .refreshToken(refreshToken)
                                                       .expiresIn(expiresIn)
                                                       .build());
    }

    @PostMapping("refreshToken")
    ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        Map<String, Object> newTokens = authenticationService.refreshToken(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.ok(AuthenticationResponse.builder()
                                                       .idToken((String) newTokens.get(ID_TOKEN))
                                                       .accessToken((String) newTokens.get(ACCESS_TOKEN))
                                                       .expiresIn((Integer) newTokens.get(EXPIRES_IN))
                                                       .build());
    }

    @PostMapping("logout")
    ResponseEntity logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }
}
