package dev.charles.SimpleService.errors.exception;

import dev.charles.SimpleService.errors.errorcode.CustomErrorCode;

public class AuthorizationException extends RestApiException {
    public AuthorizationException(String message) {
        super(CustomErrorCode.NOT_AUTHORIZED, message);
    }
}
