package com.dantal.common.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resource, Object identifier) {
        super(resource + " not found: " + identifier, HttpStatus.NOT_FOUND, "NOT_FOUND");
    }
}
