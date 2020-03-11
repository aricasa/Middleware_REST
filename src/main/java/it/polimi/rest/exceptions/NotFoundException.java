package it.polimi.rest.exceptions;

public class NotFoundException extends RestException {

    private static final long serialVersionUID = 6309092990469034744L;

    public NotFoundException() {
        this(null);
    }

    public NotFoundException(String message) {
        super(404, message);
    }

}
