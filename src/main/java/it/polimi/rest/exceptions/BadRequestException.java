package it.polimi.rest.exceptions;

public class BadRequestException extends RestException {

    private static final long serialVersionUID = -5907891550213393838L;

    public BadRequestException() {
        this(null);
    }

    public BadRequestException(String message) {
        super(400, message);
    }

}
