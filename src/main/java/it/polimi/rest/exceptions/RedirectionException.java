package it.polimi.rest.exceptions;

public class RedirectionException extends RestException {

    private static final long serialVersionUID = -4187408898798548872L;

    public final String url;

    public RedirectionException(int code, String url) {
        super(code);
        this.url = url;
    }

}
