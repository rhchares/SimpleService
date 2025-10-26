package dev.charles.SimpleService.errors.exception;

import dev.charles.SimpleService.errors.errorcode.ErrorCode;
import lombok.Getter;

@Getter
public class RestApiException extends RuntimeException {
    protected ErrorCode errorCode;
    public RestApiException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}