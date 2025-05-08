package com.Ivan.Rwalent.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotATalentException extends RuntimeException {
    public NotATalentException(String message) {
        super(message);
    }
} 