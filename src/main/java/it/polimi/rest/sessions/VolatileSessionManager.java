package it.polimi.rest.sessions;

import it.polimi.rest.exceptions.ForbiddenException;
import it.polimi.rest.exceptions.NotFoundException;
import it.polimi.rest.authorization.Token;
import it.polimi.rest.models.TokenId;

import java.util.*;
import java.util.function.Supplier;

public class VolatileSessionManager implements SessionsManager {

    private final HashMap<TokenId, Token> tokens = new HashMap<>();

    @Override
    public synchronized Token token(TokenId id) {
        Token result = tokens.values().stream()
                .filter(token -> token.id().equals(id))
                .findFirst()
                .orElseThrow(NotFoundException::new);

        if (!result.isValid()) {
            remove(result.id());
            throw new NotFoundException();
        }

        return result;
    }

    @Override
    public synchronized void add(Token token) {
        if (tokens.containsKey(token.id())) {
            throw new ForbiddenException();
        }

        tokens.put(token.id(), token);
    }

    @Override
    public synchronized void remove(TokenId id) {
        Token token = token(id);

        tokens.remove(token.id());
    }

}
