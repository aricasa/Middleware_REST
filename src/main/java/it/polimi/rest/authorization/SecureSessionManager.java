package it.polimi.rest.authorization;

import it.polimi.rest.exceptions.ForbiddenException;
import it.polimi.rest.exceptions.UnauthorizedException;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.sessions.SessionsManager;

import static it.polimi.rest.exceptions.UnauthorizedException.AuthType.BEARER;

class SecureSessionManager implements SessionsManager {

    private final SessionsManager sessionsManager;
    private final Authorizer authorizer;
    private final Agent agent;

    public SecureSessionManager(SessionsManager sessionsManager, Authorizer authorizer, Agent agent) {
        this.sessionsManager = sessionsManager;
        this.authorizer = authorizer;
        this.agent = agent;
    }

    @Override
    public TokenId getUniqueId() {
        return sessionsManager.getUniqueId();
    }

    @Override
    public Token token(TokenId id) {
        if (agent == null) {
            throw new UnauthorizedException(BEARER);
        }

        Token result = sessionsManager.token(id);

        if (!authorizer.get(result, agent).read) {
            throw new ForbiddenException();
        }

        return result;
    }

    @Override
    public void add(Token token) {
        sessionsManager.add(token);
    }

    @Override
    public void remove(TokenId id) {
        Token token = token(id);

        if (!authorizer.get(id, token.agent()).write) {
            throw new ForbiddenException();
        }

        sessionsManager.remove(id);
    }

}
