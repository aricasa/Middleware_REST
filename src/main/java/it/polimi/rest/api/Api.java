package it.polimi.rest.api;

import it.polimi.rest.authorization.SessionManager;
import it.polimi.rest.authorization.Authorizer;
import it.polimi.rest.authentication.Authenticator;
import it.polimi.rest.data.*;

public abstract class Api {

    protected final Authenticator authenticator;
    protected final SessionManager sessionManager;

    /**
     * Constructor.
     *
     * @param storage           storage
     * @param sessionManager    session manager
     */
    public Api(Storage storage, SessionManager sessionManager) {
        this.authenticator = new Authenticator(storage);
        this.sessionManager = sessionManager;
    }

}
