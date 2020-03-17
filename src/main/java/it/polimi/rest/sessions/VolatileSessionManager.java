package it.polimi.rest.sessions;

import it.polimi.rest.exceptions.ForbiddenException;
import it.polimi.rest.exceptions.NotFoundException;
import it.polimi.rest.models.Token;
import it.polimi.rest.models.TokenId;

import java.util.*;

import static java.util.UUID.randomUUID;

public class VolatileSessionManager implements SessionsManager {

    private final HashMap<TokenId, Token> tokens = new HashMap<>();
    private final Collection<TokenId> reserved = new HashSet<>();

    @Override
    public synchronized TokenId getUniqueId() {
        TokenId id;

        do {
            id = new TokenId(randomUUID().toString().split("-")[0]);
        } while (tokens.containsKey(id) || reserved.contains(id));

        // Reserve the ID
        reserved.add(id);

        return id;
    }

    @Override
    public synchronized Token token(TokenId id) {
        Token result = tokens.values().stream()
                .filter(token -> token.id.equals(id))
                .findFirst()
                .orElseThrow(NotFoundException::new);

        if (!result.isValid()) {
            remove(result.id);
            throw new NotFoundException();
        }

        return result;
    }

    @Override
    public synchronized void add(Token token) {
        if (tokens.containsKey(token.id)) {
            throw new ForbiddenException();
        }

        tokens.put(token.id, token);
        reserved.remove(token.id);
    }

    @Override
    public synchronized void remove(TokenId id) {
        tokens.remove(id);
        reserved.remove(id);
    }

}
