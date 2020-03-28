package it.polimi.rest.exceptions;

import it.polimi.rest.communication.HttpStatus;

public class UnauthorizedException extends RestException {

    private static final long serialVersionUID = -182820727825587302L;

    public enum AuthType {
        BASIC("Basic"),
        BEARER("Bearer");

        private final String type;

        AuthType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    public final AuthType authentication;

    public UnauthorizedException(AuthType authentication) {
        this(authentication, null);
    }

    public UnauthorizedException(AuthType authentication, String message) {
        super(HttpStatus.UNAUTHORIZED, message);
        this.authentication = authentication;
    }

}
