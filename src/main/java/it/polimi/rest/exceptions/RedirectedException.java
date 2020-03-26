package it.polimi.rest.exceptions;

import it.polimi.rest.communication.HttpStatus;

public class RedirectedException extends RestException {

    private static final long serialVersionUID = -4187408898798548872L;

    public final String url;

    public RedirectedException(String url) {
        super(HttpStatus.PERMANENT_REDIRECT);
        this.url = url;
    }

}
