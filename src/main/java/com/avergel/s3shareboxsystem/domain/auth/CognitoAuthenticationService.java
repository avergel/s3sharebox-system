package com.avergel.s3shareboxsystem.domain.auth;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.avergel.s3shareboxsystem.infrastructure.config.CognitoConfig;
import com.avergel.s3shareboxsystem.infrastructure.exception.model.JwtException;
import com.avergel.s3shareboxsystem.infrastructure.security.model.SpringSecurityUser;
import com.avergel.s3shareboxsystem.infrastructure.util.Constants;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
class CognitoAuthenticationService implements AuthenticationService {

    private final CognitoConfig cognitoConfig;
    private final AWSCognitoIdentityProvider cognitoClient;
    private final ConfigurableJWTProcessor configurableJWTProcessor;

    public CognitoAuthenticationService(CognitoConfig cognitoConfig, AWSCognitoIdentityProvider cognitoClient, ConfigurableJWTProcessor configurableJWTProcessor) {
        this.cognitoConfig = cognitoConfig;
        this.cognitoClient = cognitoClient;
        this.configurableJWTProcessor = configurableJWTProcessor;
    }

    @Override
    public Authentication getAuthentication(String jwtToken) {
        JWTClaimsSet claimsSet = null;
        try {
            claimsSet = configurableJWTProcessor.process(jwtToken, null);
        } catch (BadJOSEException | ParseException | JOSEException e) {
            throw new JwtException(e.getMessage());
        }
        if (!claimsSet.getIssuer()
                      .equals(cognitoConfig.getCognitoIdentityPoolUrl())) {
            throw new JwtException("Incorrect issuer " + claimsSet.getIssuer());
        }
        if (!claimsSet.getClaim("token_use")
                      .equals("id")) {
            throw new JwtException("Incorrect token_use " + claimsSet.getClaim("token_use"));
        }
        String username = claimsSet.getClaims()
                                   .get(cognitoConfig.getUserNameField())
                                   .toString();
        return new UsernamePasswordAuthenticationToken(username, claimsSet, new ArrayList<>());
    }

    @Override
    public SpringSecurityUser login(String username, String password) {

        final Map<String, String> authParams = new HashMap<>();
        authParams.put("USERNAME", username);
        authParams.put("PASSWORD", password);

        final AdminInitiateAuthRequest authRequest = new AdminInitiateAuthRequest();
        authRequest.withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                   .withClientId(cognitoConfig.getClientAppId())
                   .withUserPoolId(cognitoConfig.getPoolId())
                   .withAuthParameters(authParams);
        AdminInitiateAuthResult result = cognitoClient.adminInitiateAuth(authRequest);
        AuthenticationResultType authenticationResult = result.getAuthenticationResult();

        return SpringSecurityUser.builder()
                                 .username(username)
                                 .accessToken(authenticationResult.getAccessToken())
                                 .idToken(authenticationResult.getIdToken())
                                 .refreshToken(authenticationResult.getRefreshToken())
                                 .expiresIn(authenticationResult.getExpiresIn())
                                 .tokenType(authenticationResult.getTokenType())
                                 .build();
    }

    @Override
    public Map<String, Object> refreshToken(String refreshToken) {
        final Map<String, String> authParams = new HashMap<>();
        authParams.put("REFRESH_TOKEN", refreshToken);

        final AdminInitiateAuthRequest authRequest = new AdminInitiateAuthRequest();
        authRequest.withAuthFlow(AuthFlowType.REFRESH_TOKEN_AUTH)
                   .withClientId(cognitoConfig.getClientAppId())
                   .withUserPoolId(cognitoConfig.getPoolId())
                   .withAuthParameters(authParams);
        AdminInitiateAuthResult result = cognitoClient.adminInitiateAuth(authRequest);
        AuthenticationResultType authenticationResult = result.getAuthenticationResult();
        Map<String, Object> newTokenMap = new HashMap<>();
        newTokenMap.put(Constants.ACCESS_TOKEN, authenticationResult.getAccessToken());
        newTokenMap.put(Constants.ID_TOKEN, authenticationResult.getIdToken());
        newTokenMap.put(Constants.EXPIRES_IN, authenticationResult.getExpiresIn());

        return newTokenMap;
    }
}
