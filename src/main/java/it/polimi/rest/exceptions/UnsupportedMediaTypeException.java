package it.polimi.rest.exceptions;

import it.polimi.rest.communication.HttpStatus;

public class UnsupportedMediaTypeException extends RestException {

    private static final long serialVersionUID = 2973920502884725563L;

    public UnsupportedMediaTypeException() {
        this(null);
    }

    public UnsupportedMediaTypeException(String message) {
        super(HttpStatus.UNSUPPORTED_MEDIA_TYPE, message);
    }

}
