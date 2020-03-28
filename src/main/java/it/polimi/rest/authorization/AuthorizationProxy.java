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

    public SessionsManager sessionsManager(TokenId tokenId) {
        return sessionsManager(getToken(tokenId));
    }

    public SessionsManager sessionsManager(Token token) {
        Agent agent = token == null || !token.isValid() ? null : token.agent();
        return new SecureSessionManager(sessionsManager, authorizer, agent);
    }

    public DataProvider dataProvider(TokenId tokenId) {
        return dataProvider(getToken(tokenId));
    }

    public DataProvider dataProvider(Token token) {
        Agent agent = token == null || !token.isValid() ? null : token.agent();
        return new SecureDataProvider(dataProvider, authorizer, agent);
    }

    private Token getToken(TokenId id) {
        try {
            return sessionsManager.token(id);
        } catch (RestException e) {
            return null;
        }
    }

}
