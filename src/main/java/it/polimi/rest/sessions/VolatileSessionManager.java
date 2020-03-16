package it.polimi.rest.sessions;

import it.polimi.rest.models.Token;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.User;
import it.polimi.rest.models.UserId;

import java.util.*;

import static java.util.UUID.randomUUID;

public class VolatileSessionManager implements SessionsManager {

    private final Collection<Token> tokens = new HashSet<>();
    private final Collection<TokenId> reserved = new HashSet<>();

    @Override
    public synchronized TokenId getUniqueId() {
        TokenId id;

        do {
            id = new TokenId(randomUUID().toString().split("-")[0]);
        } while (get(id).isPresent() || reserved.contains(id));

        // Reserve the ID
        reserved.add(id);

        return id;
    }

    @Override
    public synchronized Optional<Token> get(TokenId id) {
        return tokens.stream().filter(token -> token.id.equals(id)).findFirst();
    }

    @Override
    public synchronized void add(Token token) {
        if (!get(token.id).isPresent()) {
            tokens.add(token);
        }

        reserved.remove(token.id);
    }

    @Override
    public synchronized void remove(TokenId id) {
        tokens.removeIf(token -> token.id.equals(id));
        reserved.remove(id);
    }

}
