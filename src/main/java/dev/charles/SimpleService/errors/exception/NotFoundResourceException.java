package dev.charles.SimpleService.errors.exception;

import dev.charles.SimpleService.errors.errorcode.CustomErrorCode;


public class NotFoundResourceException extends RestApiException{
    public NotFoundResourceException(String message) {
        super(CustomErrorCode.RESOURCE_NOT_FOUND, message);
    }
}
