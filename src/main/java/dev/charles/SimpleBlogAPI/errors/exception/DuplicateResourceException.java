package dev.charles.SimpleBlogAPI.errors.exception;

import dev.charles.SimpleBlogAPI.errors.errorcode.CustomErrorCode;

public class DuplicateResourceException extends RestApiException {
    public DuplicateResourceException(String message) {
        super(CustomErrorCode.DUPLICATED_RESOURCE, message);
    }
}
