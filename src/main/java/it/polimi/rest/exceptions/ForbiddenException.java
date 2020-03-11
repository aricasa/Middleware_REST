package it.polimi.rest.exceptions;

public final class ForbiddenException extends RestException {

    private static final long serialVersionUID = 1972931111655250395L;

    public ForbiddenException() {
        this(null);
    }

    public ForbiddenException(String message) {
        super(403, message);
    }

}
