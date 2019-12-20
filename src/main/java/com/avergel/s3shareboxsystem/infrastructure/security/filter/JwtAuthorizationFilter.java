package com.avergel.s3shareboxsystem.infrastructure.security.filter;

import com.avergel.s3shareboxsystem.domain.auth.AuthenticationService;
import com.avergel.s3shareboxsystem.infrastructure.exception.model.JwtException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final AuthenticationService authenticationService;

    public JwtAuthorizationFilter(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            String jwtToken = requestTokenHeader.substring(7);
            try {
                Authentication usernamePasswordAuthenticationToken = authenticationService.getAuthentication(jwtToken);
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } catch (JwtException e) {
                createExceptionResponse(request, response, e.getMessage());
                SecurityContextHolder.clearContext();
                return;
            }
        } else {
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }

    private void createExceptionResponse(ServletRequest request, ServletResponse response, String message) throws IOException {
        ObjectMapper objMapper = new ObjectMapper();
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        final HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper(httpResponse);
        wrapper.setStatus(HttpStatus.UNAUTHORIZED.value());
        wrapper.setContentType(APPLICATION_JSON_VALUE);
        Map<String, String> payload = new HashMap<>();
        payload.put("message", message);
        wrapper.getWriter().print((objMapper.writeValueAsString(payload)));
        wrapper.getWriter().flush();
    }
}