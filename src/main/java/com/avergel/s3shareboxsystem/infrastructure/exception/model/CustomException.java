package com.avergel.s3shareboxsystem.infrastructure.exception.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomException extends Exception{
    private int status;
    private String message;
}
