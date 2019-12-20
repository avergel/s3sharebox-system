package com.avergel.s3shareboxsystem.infrastructure.exception.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode
public class ExceptionResponse {
    final LocalDateTime timestamp;
    final int status;
    final String message;

    public ExceptionResponse(int status, String message) {
        timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
    }
}
