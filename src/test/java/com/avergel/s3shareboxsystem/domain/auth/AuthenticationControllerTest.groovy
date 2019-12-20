package com.avergel.s3shareboxsystem.domain.auth

import com.avergel.s3shareboxsystem.domain.auth.dto.AuthenticationRequest
import com.avergel.s3shareboxsystem.domain.auth.dto.AuthenticationResponse
import com.avergel.s3shareboxsystem.domain.auth.dto.RefreshTokenRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import spock.lang.Specification
import spock.lang.Subject

import static com.avergel.s3shareboxsystem.infrastructure.util.Constants.*

class AuthenticationControllerTest extends Specification {
    def authenticationManager = Mock AuthenticationManager
    def authenticationService = Mock AuthenticationService
    def username = "username"
    def password = "password"
    def accessToken = "accessToken"
    def idToken = "idToken"
    def refreshToken = "refreshToken"
    def expiresIn = 3600

    @Subject
    def controller = new AuthenticationController(authenticationManager, authenticationService)

    def "Login"() {
        given:
        def authenticatedCredentials = new HashMap<String, Object>()
        authenticatedCredentials.put(ACCESS_TOKEN, accessToken)
        authenticatedCredentials.put(ID_TOKEN, idToken)
        authenticatedCredentials.put(REFRESH_TOKEN, refreshToken)
        authenticatedCredentials.put(EXPIRES_IN, expiresIn)
        def authentication = new UsernamePasswordAuthenticationToken(username, authenticatedCredentials, new ArrayList<>())

        def request = new AuthenticationRequest(username, password)
        def expected = ResponseEntity.ok(
                AuthenticationResponse.builder()
                        .username(username)
                        .idToken(idToken)
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .expiresIn(expiresIn)
                        .build())

        when:
        def actual = controller.login(request)

        then:
        1 * authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password)) >> authentication

        and:
        expected == actual

    }

    def "RefreshToken"() {
        given:
        def request = new RefreshTokenRequest(refreshToken)
        def newTokens = new HashMap <String, Object>()
        newTokens.put(ACCESS_TOKEN, accessToken)
        newTokens.put(ID_TOKEN, idToken)
        newTokens.put(REFRESH_TOKEN, refreshToken)
        newTokens.put(EXPIRES_IN, expiresIn)
        def expected = ResponseEntity.ok(AuthenticationResponse.builder()
                .idToken(idToken)
                .accessToken(accessToken)
                .expiresIn(expiresIn)
                .build())

        when:
        def actual = controller.refreshToken(request)

        then:
        1 * authenticationService.refreshToken(refreshToken) >> newTokens

        and:
        expected == actual
    }
}
