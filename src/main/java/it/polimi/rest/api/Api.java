package it.polimi.rest.api;

import it.polimi.rest.authorization.AuthorizationProxy;
import it.polimi.rest.authorization.Authorizer;
import it.polimi.rest.authentication.CredentialsManager;
import it.polimi.rest.data.*;

public abstract class Api {

    protected final AuthorizationProxy proxy;
    protected final CredentialsManager credentialsManager;

    /**
     * Constructor.
     *
     * @param authorizer        authorizer
     * @param sessionsManager   sessions manager
     * @param storage           storage
     */
    public Api(Authorizer authorizer, SessionsManager sessionsManager, Storage storage) {
        DataProvider dataProvider = new BaseDataProvider(storage);
        this.credentialsManager = new CredentialsManager(dataProvider);
        this.proxy = new AuthorizationProxy(authorizer, sessionsManager, dataProvider);
    }

}
