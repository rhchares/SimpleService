package dev.charles.SimpleBlogAPI.errors.exception;

import dev.charles.SimpleBlogAPI.errors.errorcode.CustomErrorCode;

public class NotAuthorizedException extends RestApiException {
    public NotAuthorizedException(String message) {
        super(CustomErrorCode.NOT_AUTHORIZED, message);
    }
}
