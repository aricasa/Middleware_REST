package it.polimi.rest.api;

import it.polimi.rest.authorization.AuthorizationProxy;
import it.polimi.rest.authorization.Authorizer;
import it.polimi.rest.communication.Responder;
import it.polimi.rest.communication.TokenExtractor;
import it.polimi.rest.credentials.CredentialsManager;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.sessions.SessionsManager;
import it.polimi.rest.utils.Logger;

import java.util.function.Function;

public abstract class Api {

    protected final AuthorizationProxy proxy;
    protected final CredentialsManager credentialsManager;

    public Api(Authorizer authorizer,
               SessionsManager sessionsManager,
               DataProvider dataProvider,
               CredentialsManager credentialsManager) {

        this.proxy = new AuthorizationProxy(authorizer, sessionsManager, dataProvider);
        this.credentialsManager = credentialsManager;
    }

}
