package dev.charles.SimpleService.errors.exception;

import dev.charles.SimpleService.errors.errorcode.CustomErrorCode;

public class DuplicateResourceException extends RestApiException {
    public DuplicateResourceException(String message) {
        super(CustomErrorCode.DUPLICATED_RESOURCE, message);
    }
}
