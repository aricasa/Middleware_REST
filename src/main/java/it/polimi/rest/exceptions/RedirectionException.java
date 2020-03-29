package it.polimi.rest.exceptions;

import it.polimi.rest.communication.HttpStatus;

public class RedirectionException extends RestException {

    private static final long serialVersionUID = -4187408898798548872L;

    public final String url;

    public RedirectionException(String url) {
        super(HttpStatus.FOUND);
        this.url = url;
    }

}
