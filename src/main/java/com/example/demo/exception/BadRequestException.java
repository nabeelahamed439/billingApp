package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    private final String localMessage;
    private final String message;
    private final HttpStatus status;

    public BadRequestException(String message, String localMessage) {
        super(message);
        this.localMessage = localMessage;
        this.message = message;
        this.status = HttpStatus.BAD_REQUEST;
    }
} 