package it.polimi.rest.exceptions;

import it.polimi.rest.communication.HttpStatus;

public final class ForbiddenException extends RestException {

    private static final long serialVersionUID = 1972931111655250395L;

    public ForbiddenException() {
        this(null);
    }

    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }

}
