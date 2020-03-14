package it.polimi.rest.exceptions;

import it.polimi.rest.messages.HttpStatus;

public class BadRequestException extends RestException {

    private static final long serialVersionUID = -5907891550213393838L;

    public BadRequestException() {
        this(null);
    }

    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

}
