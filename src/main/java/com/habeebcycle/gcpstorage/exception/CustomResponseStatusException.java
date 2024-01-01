package com.habeebcycle.gcpstorage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CustomResponseStatusException extends ResponseStatusException {

    private final HttpStatus httpStatus;
    private final String errorMessage;

    public CustomResponseStatusException(HttpStatus httpStatus, String errorMessage) {
        super(httpStatus, errorMessage);
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public int getHttpStatusCode() {
        return httpStatus.value();
    }

    @Override
    public String toString() {
        return "CustomResponseStatusException{" +
                "httpStatus=" + httpStatus +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
