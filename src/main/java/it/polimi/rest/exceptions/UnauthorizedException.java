package it.polimi.rest.exceptions;

public class UnauthorizedException extends RestException {

    private static final long serialVersionUID = -182820727825587302L;

    public UnauthorizedException() {
        this(null);
    }

    public UnauthorizedException(String message) {
        super(401, message);
    }

}
