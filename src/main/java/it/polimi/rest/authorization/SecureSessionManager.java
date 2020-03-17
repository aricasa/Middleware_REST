package it.polimi.rest.authorization;

import it.polimi.rest.exceptions.ForbiddenException;
import it.polimi.rest.exceptions.UnauthorizedException;
import it.polimi.rest.models.Token;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.sessions.SessionsManager;

class SecureSessionManager implements SessionsManager {

    private final SessionsManager sessionsManager;
    private final Authorizer authorizer;
    private final Token token;

    public SecureSessionManager(SessionsManager sessionsManager, Authorizer authorizer, Token token) {
        this.sessionsManager = sessionsManager;
        this.authorizer = authorizer;
        this.token = token;
    }

    @Override
    public TokenId getUniqueId() {
        return sessionsManager.getUniqueId();
    }

    @Override
    public Token token(TokenId id) {
        if (token == null || !token.isValid()) {
            throw new UnauthorizedException();
        }

        Token result = sessionsManager.token(id);

        if (!authorizer.check(token, result).read) {
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
        if (!authorizer.check(token, token(id)).write) {
            throw new ForbiddenException();
        }
    }

}
