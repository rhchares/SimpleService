package dev.charles.SimpleBlogAPI.errors.exception;

import dev.charles.SimpleBlogAPI.errors.errorcode.CustomErrorCode;


public class NotFoundResourceException extends RestApiException{
    public NotFoundResourceException(String message) {
        super(CustomErrorCode.RESOURCE_NOT_FOUND, message);
    }
}
