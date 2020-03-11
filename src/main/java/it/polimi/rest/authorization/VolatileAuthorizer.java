package it.polimi.rest.authorization;

import it.polimi.rest.models.User;

import java.util.*;

public final class VolatileAuthorizer extends Authorizer {

    /* Number of seconds which represent the period of time in which a token is valid */
    private final static int tokenLifetime = 60 * 60;

    private final Collection<Token> allTokens = new HashSet<>();
    private final Map<User, Token> tokensByUser = new HashMap<>();

    public VolatileAuthorizer() {
        super(tokenLifetime);
    }

    @Override
    public Optional<Token> searchToken(String id) {
        return allTokens.parallelStream().filter(token -> token.id.equals(id)).findFirst();
    }

    @Override
    protected Optional<Token> getByUser(User user) {
        return Optional.ofNullable(tokensByUser.get(user));
    }

    @Override
    protected void add(Token token) {
        allTokens.add(token);
        tokensByUser.put(token.owner, token);
    }

    @Override
    protected void remove(Token token) {
        allTokens.remove(token);
        Optional.ofNullable(tokensByUser.get(token.owner)).ifPresent(found -> tokensByUser.remove(found.owner));
    }

}
