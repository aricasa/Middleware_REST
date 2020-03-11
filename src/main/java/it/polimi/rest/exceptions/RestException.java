package it.polimi.rest.exceptions;

import com.google.gson.annotations.Expose;

public abstract class RestException extends RuntimeException {

    private static final long serialVersionUID = 8900729317214269172L;

    public final int code;

    @Expose
    public final String error;

    public RestException(int code) {
        this(code, null);
    }

    public RestException(int code, String message) {
        this.code = code;
        this.error = message;
    }

}
