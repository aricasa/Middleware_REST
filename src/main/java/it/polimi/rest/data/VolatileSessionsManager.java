package it.polimi.rest.data;

import it.polimi.rest.exceptions.ForbiddenException;
import it.polimi.rest.exceptions.NotFoundException;
import it.polimi.rest.authorization.Token;
import it.polimi.rest.models.TokenId;

import java.util.*;

public class VolatileSessionsManager implements SessionsManager {

    private final Collection<Token> tokens = new HashSet<>();

    @Override
    public Token token(TokenId id) {
        Token result = tokens.stream()
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
    public void add(Token token) {
        if (tokens.stream().anyMatch(t -> t.id().equals(token.id()))) {
            throw new ForbiddenException();
        }

        tokens.add(token);
    }

    @Override
    public void remove(TokenId id) {
        Token token = token(id);
        tokens.removeIf(t -> t.id().equals(token.id()));
    }

}
