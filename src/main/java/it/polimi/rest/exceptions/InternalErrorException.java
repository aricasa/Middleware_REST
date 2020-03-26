package it.polimi.rest.exceptions;

import it.polimi.rest.communication.HttpStatus;

public class InternalErrorException extends RestException {

    private static final long serialVersionUID = 2246634768685755871L;

    public InternalErrorException() {
        this(null);
    }

    public InternalErrorException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

}
