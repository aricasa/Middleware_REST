package it.polimi.rest.exceptions;

import it.polimi.rest.communication.HttpStatus;

public final class NotFoundException extends RestException {

    private static final long serialVersionUID = 6309092990469034744L;

    public NotFoundException() {
        this(null);
    }

    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

}
