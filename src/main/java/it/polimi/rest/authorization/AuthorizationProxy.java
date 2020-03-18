package it.polimi.rest.authorization;

import it.polimi.rest.data.DataProvider;
import it.polimi.rest.exceptions.RestException;
import it.polimi.rest.models.*;
import it.polimi.rest.sessions.SessionsManager;

public class AuthorizationProxy {

    private final Authorizer authorizer;
    private final SessionsManager sessionsManager;
    private final DataProvider dataProvider;

    public AuthorizationProxy(Authorizer authorizer,
                              SessionsManager sessionsManager,
                              DataProvider dataProvider) {

        this.authorizer = authorizer;
        this.sessionsManager = sessionsManager;
        this.dataProvider = dataProvider;
    }

    public SessionsManager getSessionsManager(TokenId tokenId) {
        return new SecureSessionManager(sessionsManager, authorizer, getToken(tokenId));
    }

    public DataProvider getDataProvider(TokenId tokenId) {
        return new SecureDataProvider(dataProvider, authorizer, getToken(tokenId));
    }

    private Token getToken(TokenId id) {
        try {
            return sessionsManager.token(id);
        } catch (RestException e) {
            return null;
        }
    }

}
