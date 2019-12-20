package com.avergel.s3shareboxsystem.infrastructure.exception;

import com.amazonaws.services.cognitoidp.model.NotAuthorizedException;
import com.avergel.s3shareboxsystem.infrastructure.exception.dto.ExceptionResponse;
import com.avergel.s3shareboxsystem.infrastructure.exception.model.CustomException;
import com.avergel.s3shareboxsystem.infrastructure.exception.model.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity handleNotAuthorizedException(HttpServletRequest request, NotAuthorizedException e) {
        return ResponseEntity.badRequest()
                             .body(new ExceptionResponse(HttpStatus.BAD_REQUEST.value(),
                                     "Incorrect username or password"));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity handleJwtException(HttpServletRequest request, JwtException e) {
        return ResponseEntity.badRequest()
                             .body(new ExceptionResponse(HttpStatus.BAD_REQUEST.value(),
                                     "Invalid Token"));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity handleCustomException(HttpServletRequest request, CustomException e) {
        Map<String, String> body = new HashMap<>();
        body.put("message", e.getMessage());
        return ResponseEntity.status(e.getStatus())
                             .body(body);
    }

}
